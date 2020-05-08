package com.viettel.backend.service.common;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.viettel.backend.domain.Area;
import com.viettel.backend.domain.Client;
import com.viettel.backend.domain.Config;
import com.viettel.backend.domain.Customer;
import com.viettel.backend.domain.CustomerType;
import com.viettel.backend.domain.Distributor;
import com.viettel.backend.domain.PO;
import com.viettel.backend.domain.POSearchable;
import com.viettel.backend.domain.Product;
import com.viettel.backend.domain.ProductCategory;
import com.viettel.backend.domain.Route;
import com.viettel.backend.domain.UOM;
import com.viettel.backend.domain.User;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.domain.embed.CategoryEmbed;
import com.viettel.backend.domain.embed.Schedule;
import com.viettel.backend.domain.embed.ScheduleItem;
import com.viettel.backend.domain.embed.UserEmbed;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.repository.ClientRepository;
import com.viettel.backend.repository.CodeGeneratorRepository;
import com.viettel.backend.repository.ConfigRepository;
import com.viettel.backend.repository.MasterDataRepository;
import com.viettel.backend.repository.UserRepository;
import com.viettel.backend.repository.common.CacheRepository;
import com.viettel.backend.util.DateTimeUtils;
import com.viettel.backend.util.LocationUtils;
import com.viettel.backend.util.PasswordUtils;
import com.viettel.backend.util.StringUtils;

import reactor.bus.EventBus;

@Service
public class MasterDataService extends AbstractService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private EventBus eventBus;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CodeGeneratorRepository codeGeneratorRepository;

    @Autowired
    private ConfigRepository configRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private MasterDataRepository masterDataRepository;
    
    @Autowired
    private CacheRepository cacheRepository;
    
    @Autowired
    private CacheService cacheService;

    public void importMasterData(ObjectId clientId, InputStream inputStream) {
        Assert.notNull(clientId);
        Assert.notNull(inputStream);

        clearDatas(clientId);

        Config config = configRepository.getConfig(clientId);
        User defaultAdmin = userRepository.getDefaultAdmin(clientId);
        Client client = clientRepository.getById(PO.CLIENT_ROOT_ID, clientId);

        HashMap<String, User> supervisorMap = new HashMap<>();
        HashMap<String, Distributor> distributorMap = new HashMap<>();
        List<User> distributorAdmins = new LinkedList<>();
        HashMap<String, User> salesmanMap = new HashMap<>();
        HashMap<String, UOM> uomMap = new HashMap<>();
        HashMap<String, ProductCategory> productCategoryMap = new HashMap<>();
        List<Product> products = new LinkedList<>();
        HashMap<String, Area> areaMap = new HashMap<>();
        HashMap<String, CustomerType> customerTypeMap = new HashMap<>();
        HashMap<String, Route> routeMap = new HashMap<>();
        List<Customer> customers = new LinkedList<>();

        XSSFWorkbook wb = null;
        try {
            wb = new XSSFWorkbook(inputStream);
            int lastRowNum = 1;

            // SUPERVISOR
            XSSFSheet supervisorSheet = wb.getSheetAt(0);
            lastRowNum = supervisorSheet.getLastRowNum();
            for (int i = 1; i <= lastRowNum; i++) {
                XSSFRow row = supervisorSheet.getRow(i);
                String fullname = getStringCellValue(row.getCell(0));
                String username = getStringCellValue(row.getCell(1));

                User supervisor = new User();
                supervisor.setDraft(false);
                supervisor.setPassword(PasswordUtils.getDefaultPassword(passwordEncoder));
                supervisor.setUsername(client.getCode(), username);
                supervisor.setFullname(fullname);
                supervisor.setRole(Role.SUPERVISOR);
                initDomain(clientId, supervisor);
                supervisorMap.put(username.toLowerCase(), supervisor);
            }

            // DISTRIBUTOR
            XSSFSheet distributorSheet = wb.getSheetAt(1);
            lastRowNum = distributorSheet.getLastRowNum();
            for (int i = 1; i <= lastRowNum; i++) {
                XSSFRow row = distributorSheet.getRow(i);
                String name = getStringCellValue(row.getCell(0));
                String supervisorUsername = getStringCellValue(row.getCell(1));
                String username = getStringCellValue(row.getCell(2));

                User supervisor = supervisorMap.get(supervisorUsername.toLowerCase());
                BusinessAssert.notNull(supervisor);
                UserEmbed supervisorEmbed = new UserEmbed(supervisor);

                // DISTRIBUTOR
                Distributor distributor = new Distributor();
                distributor.setName(name);
                distributor.setCode(codeGeneratorRepository.getDistributorCode(clientId.toString()));
                distributor.setSupervisor(supervisorEmbed);
                initDomain(clientId, distributor);
                distributorMap.put(name.toLowerCase(), distributor);

                // DISTRIBUTOR ADMIN
                User distributorAdmin = new User();
                distributorAdmin.setPassword(PasswordUtils.getDefaultPassword(passwordEncoder));
                distributorAdmin.setUsername(client.getCode(), username);
                distributorAdmin.setFullname(distributor.getName());
                distributorAdmin.setRole(Role.DISTRIBUTOR);
                CategoryEmbed distributorEmbed = new CategoryEmbed(distributor);
                distributorAdmin.setDistributor(distributorEmbed);
                initDomain(clientId, distributorAdmin);
                distributorAdmins.add(distributorAdmin);
            }

            // SALESMAN
            XSSFSheet salesmanSheet = wb.getSheetAt(2);
            lastRowNum = salesmanSheet.getLastRowNum();
            for (int i = 1; i <= lastRowNum; i++) {
                XSSFRow row = salesmanSheet.getRow(i);
                String fullname = getStringCellValue(row.getCell(0));
                String username = getStringCellValue(row.getCell(1));
                String distributorName = getStringCellValue(row.getCell(2));

                Distributor distributor = distributorMap.get(distributorName.toLowerCase());
                BusinessAssert.notNull(distributor);
                CategoryEmbed distributorEmbed = new CategoryEmbed(distributor);

                User salesman = new User();
                salesman.setPassword(PasswordUtils.getDefaultPassword(passwordEncoder));
                salesman.setUsername(client.getCode(), username);
                salesman.setFullname(fullname);
                salesman.setRole(Role.SALESMAN);
                salesman.setDistributor(distributorEmbed);
                initDomain(clientId, salesman);
                salesmanMap.put(username.toLowerCase(), salesman);
            }

            // UOM
            XSSFSheet uomSheet = wb.getSheetAt(3);
            lastRowNum = uomSheet.getLastRowNum();
            for (int i = 1; i <= lastRowNum; i++) {
                XSSFRow row = uomSheet.getRow(i);
                String name = getStringCellValue(row.getCell(0));
                String code = getStringCellValue(row.getCell(1));

                UOM uom = new UOM();
                uom.setName(name);
                uom.setCode(code);
                initDomain(clientId, uom);
                uomMap.put(name.toLowerCase(), uom);
            }

            // PRODUCT CATEGORY
            XSSFSheet productCategorySheet = wb.getSheetAt(4);
            lastRowNum = productCategorySheet.getLastRowNum();
            for (int i = 1; i <= lastRowNum; i++) {
                XSSFRow row = productCategorySheet.getRow(i);
                String name = getStringCellValue(row.getCell(0));

                ProductCategory productCategory = new ProductCategory();
                productCategory.setName(name);
                initDomain(clientId, productCategory);
                productCategoryMap.put(name.toLowerCase(), productCategory);
            }

            // PRODUCT
            XSSFSheet productSheet = wb.getSheetAt(5);
            lastRowNum = productSheet.getLastRowNum();
            for (int i = 1; i <= lastRowNum; i++) {
                XSSFRow row = productSheet.getRow(i);
                String name = getStringCellValue(row.getCell(0));
                String code = getStringCellValue(row.getCell(1));
                String uomName = getStringCellValue(row.getCell(2));
                String productCategoryName = getStringCellValue(row.getCell(3));
                Double price = getNumericCellValue(row.getCell(4));
                Double productivity = getNumericCellValue(row.getCell(5));
                String description = getStringCellValue(row.getCell(6));

                UOM uom = uomMap.get(uomName.toLowerCase());
                BusinessAssert.notNull(uom);
                CategoryEmbed uomEmbed = new CategoryEmbed(uom);

                ProductCategory productCategory = productCategoryMap.get(productCategoryName.toLowerCase());
                BusinessAssert.notNull(productCategory);
                CategoryEmbed productCategoryEmbed = new CategoryEmbed(productCategory);

                Product product = new Product();
                product.setName(name);
                product.setCode(code);
                product.setUom(uomEmbed);
                product.setProductCategory(productCategoryEmbed);
                product.setPrice(new BigDecimal(price));
                product.setProductivity(new BigDecimal(productivity));
                product.setPhoto(config.getProductPhoto());
                product.setDescription(description);
                initDomain(clientId, product);
                products.add(product);
            }

            // AREA
            XSSFSheet areaSheet = wb.getSheetAt(6);
            lastRowNum = areaSheet.getLastRowNum();
            for (int i = 1; i <= lastRowNum; i++) {
                XSSFRow row = areaSheet.getRow(i);
                String name = getStringCellValue(row.getCell(0));
                String distributorName = getStringCellValue(row.getCell(1));

                Distributor distributor = distributorMap.get(distributorName.toLowerCase());
                BusinessAssert.notNull(distributor);
                CategoryEmbed distributorEmbed = new CategoryEmbed(distributor);

                Area area = new Area();
                area.setName(name);
                area.setDistributor(distributorEmbed);
                initDomain(clientId, area);
                areaMap.put(name.toLowerCase(), area);
            }

            // CUSTOMER TYPE
            XSSFSheet customerTypeSheet = wb.getSheetAt(7);
            lastRowNum = customerTypeSheet.getLastRowNum();
            for (int i = 1; i <= lastRowNum; i++) {
                XSSFRow row = customerTypeSheet.getRow(i);
                String name = getStringCellValue(row.getCell(0));

                CustomerType customerType = new CustomerType();
                customerType.setName(name);
                initDomain(clientId, customerType);
                customerTypeMap.put(name.toLowerCase(), customerType);
            }

            // ROUTE
            XSSFSheet routeSheet = wb.getSheetAt(8);
            lastRowNum = routeSheet.getLastRowNum();
            for (int i = 1; i <= lastRowNum; i++) {
                XSSFRow row = routeSheet.getRow(i);
                String name = getStringCellValue(row.getCell(0));
                String distributorName = getStringCellValue(row.getCell(1));
                String salesmanUsername = getStringCellValue(row.getCell(2));

                Distributor distributor = distributorMap.get(distributorName.toLowerCase());
                BusinessAssert.notNull(distributor);
                CategoryEmbed distributorEmbed = new CategoryEmbed(distributor);

                UserEmbed salesmanEmbed = null;
                if (salesmanUsername != null) {
                    User salesman = salesmanMap.get(salesmanUsername.toLowerCase());
                    BusinessAssert.notNull(salesman);
                    salesmanEmbed = new UserEmbed(salesman);
                }

                Route route = new Route();
                route.setName(name);
                route.setDistributor(distributorEmbed);
                if (salesmanEmbed != null) {
                    route.setSalesman(salesmanEmbed);
                }
                initDomain(clientId, route);
                routeMap.put(name.toLowerCase(), route);
            }

            // CUSTOMER
            XSSFSheet customerSheet = wb.getSheetAt(9);
            lastRowNum = customerSheet.getLastRowNum();
            for (int i = 1; i <= lastRowNum; i++) {
                XSSFRow row = customerSheet.getRow(i);
                String name = getStringCellValue(row.getCell(0));
                String distributorName = getStringCellValue(row.getCell(1));
                String areaName = getStringCellValue(row.getCell(2));
                String customerTypeName = getStringCellValue(row.getCell(3));
                String mobile = getStringCellValue(row.getCell(4));
                String phone = getStringCellValue(row.getCell(5));
                String contact = getStringCellValue(row.getCell(6));
                String email = getStringCellValue(row.getCell(7));
                Double latitude = getNumericCellValue(row.getCell(8));
                Double longitude = getNumericCellValue(row.getCell(9));
                String routeName = getStringCellValue(row.getCell(10));

                Distributor distributor = distributorMap.get(distributorName.toLowerCase());
                BusinessAssert.notNull(distributor);
                CategoryEmbed distributorEmbed = new CategoryEmbed(distributor);

                Area area = areaMap.get(areaName.toLowerCase());
                BusinessAssert.notNull(area);
                CategoryEmbed areaEmbed = new CategoryEmbed(area);

                CustomerType customerType = customerTypeMap.get(customerTypeName.toLowerCase());
                BusinessAssert.notNull(customerType);
                CategoryEmbed customerTypeEmbed = new CategoryEmbed(customerType);

                Customer customer = new Customer();
                customer.setName(name);
                customer.setApproveStatus(Customer.APPROVE_STATUS_APPROVED);
                customer.setCreatedTime(DateTimeUtils.getCurrentTime());
                customer.setCreatedBy(new UserEmbed(defaultAdmin));
                customer.setCode(codeGeneratorRepository.getCustomerCode(clientId.toString()));
                customer.setMobile(mobile);
                customer.setDistributor(distributorEmbed);
                customer.setCustomerType(customerTypeEmbed);
                customer.setArea(areaEmbed);
                customer.setPhone(phone);
                customer.setContact(contact);
                customer.setEmail(email);
                if (LocationUtils.checkLocationValid(longitude, latitude)){
                    customer.setLocation(new double[] { longitude, latitude });
                } else {
                    customer.setLocation(new double[] { config.getLocation().getLongitude(), config.getLocation().getLatitude() });
                }

                if (routeName != null) {
                    Route route = routeMap.get(routeName.toLowerCase());
                    BusinessAssert.notNull(route);

                    Schedule schedule = new Schedule();
                    schedule.setRouteId(route.getId());
                    ScheduleItem item = new ScheduleItem();
                    item.setMonday(!StringUtils.isNullOrEmpty(getStringCellValue(row.getCell(11))));
                    item.setTuesday(!StringUtils.isNullOrEmpty(getStringCellValue(row.getCell(12))));
                    item.setWednesday(!StringUtils.isNullOrEmpty(getStringCellValue(row.getCell(13))));
                    item.setThursday(!StringUtils.isNullOrEmpty(getStringCellValue(row.getCell(14))));
                    item.setFriday(!StringUtils.isNullOrEmpty(getStringCellValue(row.getCell(15))));
                    item.setSaturday(!StringUtils.isNullOrEmpty(getStringCellValue(row.getCell(16))));
                    item.setSunday(!StringUtils.isNullOrEmpty(getStringCellValue(row.getCell(17))));
                    schedule.setItems(Collections.singletonList(item));
                    customer.setSchedule(schedule);
                }
                initDomain(clientId, customer);
                customers.add(customer);
            }
        } catch (IOException e) {
            logger.error("error when read excel for import customer", e);
            throw new UnsupportedOperationException();
        } finally {
            try {
                wb.close();
            } catch (IOException e) {
                logger.error("error when close excel for import customer", e);
                throw new UnsupportedOperationException();
            }
        }

        masterDataRepository._insertBatch(clientId, User.class, supervisorMap.values());
        masterDataRepository._insertBatch(clientId, Distributor.class, distributorMap.values());
        masterDataRepository._insertBatch(clientId, User.class, distributorAdmins);
        masterDataRepository._insertBatch(clientId, User.class, salesmanMap.values());
        masterDataRepository._insertBatch(clientId, UOM.class, uomMap.values());
        masterDataRepository._insertBatch(clientId, ProductCategory.class, productCategoryMap.values());
        masterDataRepository._insertBatch(clientId, Product.class, products);
        masterDataRepository._insertBatch(clientId, Area.class, areaMap.values());
        masterDataRepository._insertBatch(clientId, CustomerType.class, customerTypeMap.values());
        masterDataRepository._insertBatch(clientId, Route.class, routeMap.values());
        masterDataRepository._insertBatch(clientId, Customer.class, customers);
    }

    private void initDomain(ObjectId clientId, PO domain) {
        domain.setId(new ObjectId());
        domain.setClientId(clientId);
        domain.setActive(true);
        domain.setDraft(false);
        
        if (domain instanceof POSearchable) {
            POSearchable searchableDomain = (POSearchable) domain;
            String searchValue = "";

            String[] sources = searchableDomain.getSearchValues();
            if (sources != null && sources.length > 0) {
                String value = StringUtils.arrayToDelimitedString(sources, " ");
                searchValue = StringUtils.getSearchableString(value);
            }

            searchableDomain.setSearch(searchValue);
        }
    }
    
    private void clearDatas(ObjectId clientId) {
        List<User> users = masterDataRepository.getAllUsers(clientId);
        for (User user : users) {
            eventBus.notify(UserDeactivationEvent.EVENT_NAME, new UserDeactivationEvent(user));
        }
        
        masterDataRepository.deleteDatas(clientId);
        
        cacheRepository.clearByClient(clientId);
        cacheService.clearByClient(clientId);
    }

    private static String getStringCellValue(XSSFCell cell) {
        if (cell == null) {
            return null;
        }

        String value = null;

        try {
            value = cell.getStringCellValue();
        } catch (Exception e) {
            Double valueDouble = getNumericCellValue(cell);
            if (valueDouble != null) {
                value = valueDouble.toString();
                if (value.endsWith(".0")) {
                    value = value.substring(0, value.length() - 2);
                }
            } else {
                return null;
            }
        }

        if (value == null) {
            return null;
        }

        value = value.trim();
        return value;
    }

    private static Double getNumericCellValue(XSSFCell cell) {
        if (cell == null) {
            return null;
        }

        Double value = null;

        try {
            value = cell.getNumericCellValue();
        } catch (Exception e) {
            return null;
        }

        return value;
    }

}
