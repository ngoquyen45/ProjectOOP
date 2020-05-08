package com.viettel.backend.service.imp.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viettel.backend.domain.Area;
import com.viettel.backend.domain.Customer;
import com.viettel.backend.domain.CustomerType;
import com.viettel.backend.domain.Distributor;
import com.viettel.backend.domain.User;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.domain.embed.CategoryEmbed;
import com.viettel.backend.domain.embed.UserEmbed;
import com.viettel.backend.dto.imp.ImportConfirmDto;
import com.viettel.backend.dto.imp.ImportConfirmDto.RowData;
import com.viettel.backend.dto.imp.ImportResultDto;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.AreaRepository;
import com.viettel.backend.repository.CodeGeneratorRepository;
import com.viettel.backend.repository.CustomerRepository;
import com.viettel.backend.repository.CustomerTypeRepository;
import com.viettel.backend.repository.DistributorRepository;
import com.viettel.backend.repository.UserRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.common.AbstractImportService;
import com.viettel.backend.service.imp.ImportCustomerService;
import com.viettel.backend.util.DateTimeUtils;
import com.viettel.backend.util.StringUtils;

@RolePermission(value = { Role.ADMIN, Role.SUPERVISOR })
@Service
public class ImportCustomerServiceImpl extends AbstractImportService implements ImportCustomerService {

    @Autowired
    private CustomerTypeRepository customerTypeRepository;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CodeGeneratorRepository codeGeneratorRepository;

    @Autowired
    private DistributorRepository distributorRepository;

    // PUBLIC
    @Override
    public byte[] getImportCustomerTemplate(UserLogin userLogin, String _distributorId, String lang) {
        lang = getLang(lang);
        
        Distributor distributor = getMandatoryPO(userLogin, _distributorId, distributorRepository);
        BusinessAssert.isTrue(checkAccessible(userLogin, distributor.getId()), "distributor not accessible");

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet customerSheet = workbook.createSheet(translate(lang, "customer"));
        XSSFRow row = customerSheet.createRow(0);
        row.createCell(0).setCellValue(translate(lang, "name") + "*");
        row.createCell(1).setCellValue(translate(lang, "customer.type") + "*");
        row.createCell(2).setCellValue(translate(lang, "area") + "*");
        row.createCell(3).setCellValue(translate(lang, "mobile") + "*");
        row.createCell(4).setCellValue(translate(lang, "phone"));
        row.createCell(5).setCellValue(translate(lang, "contact"));
        row.createCell(6).setCellValue(translate(lang, "email"));
        row.createCell(7).setCellValue(translate(lang, "latitude") + "*");
        row.createCell(8).setCellValue(translate(lang, "longitude") + "*");

        // CUSTOMER TYPE
        List<CustomerType> customerTypes = customerTypeRepository.getAll(userLogin.getClientId(), null);
        int index = 0;
        XSSFSheet routeSheet = workbook.createSheet(translate(lang, "customer.type"));
        for (CustomerType customerType : customerTypes) {
            row = routeSheet.createRow(index);
            row.createCell(0).setCellValue(customerType.getName());
            index++;
        }
        
        // AREA
        List<Area> areas = areaRepository.getAll(userLogin.getClientId(), distributor.getId());
        index = 0;
        XSSFSheet areaSheet = workbook.createSheet(translate(lang, "area"));
        for (Area area : areas) {
            row = areaSheet.createRow(index);
            row.createCell(0).setCellValue(area.getName());
            index++;
        }
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            workbook.write(baos);
            workbook.close();

            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private I_CellValidator[] getValidators(UserLogin userLogin) {
        Map<String, CustomerType> customerTypeMap = getCustomerTypeMap(userLogin.getClientId());
        Map<String, Area> areaMap = getAreaMap(userLogin.getClientId());

        List<Customer> customers = customerRepository.getAll(userLogin.getClientId(), null);
        HashSet<String> customerNames = new HashSet<String>();
        for (Customer customer : customers) {
            if (!StringUtils.isNullOrEmpty(customer.getName(), true)) {
                customerNames.add(customer.getName().trim().toUpperCase());
            }
        }

        I_CellValidator[] validators = new I_CellValidator[] {
                new MultiCellValidator(new StringMandatoryCellValidator(), new StringUniqueCellValidator(customerNames),
                        new MaxLengthCellValidator(50)),
                new MultiCellValidator(new StringMandatoryCellValidator(),
                        new ReferenceCellValidator<CustomerType>(customerTypeMap)),
                new MultiCellValidator(new StringMandatoryCellValidator(),
                        new ReferenceCellValidator<Area>(areaMap)),
                new MultiCellValidator(new StringMandatoryCellValidator(), new MaxLengthCellValidator(15)), null, null,
                null, new LatitudeCellValidator(), new LongitudeCellValidator() };
        return validators;
    }

    @Override
    public ImportConfirmDto verify(UserLogin userLogin, String _distributorId, String fileId) {
        Distributor distributor = getMandatoryPO(userLogin, _distributorId, distributorRepository);
        BusinessAssert.isTrue(checkAccessible(userLogin, distributor.getId()), "distributor not accessible");
        
        I_CellValidator[] validators = getValidators(userLogin);
        return getErrorRows(userLogin, fileId, validators);
    }

    @Override
    public ImportResultDto importCustomer(UserLogin userLogin, String _distributorId, String fileId) {
        Distributor distributor = getMandatoryPO(userLogin, _distributorId, distributorRepository);
        BusinessAssert.isTrue(checkAccessible(userLogin, distributor.getId()), "distributor not accessible");

        User currentUser = userRepository.getById(userLogin.getClientId(), userLogin.getUserId());

        I_CellValidator[] validators = getValidators(userLogin);
        ImportConfirmDto dto = getValidRows(userLogin, fileId, validators);

        List<String> codes = codeGeneratorRepository.getBatchCustomerCode(userLogin.getClientId().toString(),
                dto.getRowDatas().size());

        List<Customer> customersToInsert = new ArrayList<Customer>(dto.getRowDatas().size());
        int index = 0;
        for (RowData row : dto.getRowDatas()) {
            Customer customer = new Customer();

            initPOWhenCreate(Customer.class, userLogin, customer);
            customer.setDraft(false);
            customer.setApproveStatus(Customer.APPROVE_STATUS_APPROVED);
            customer.setCreatedTime(DateTimeUtils.getCurrentTime());
            customer.setCreatedBy(new UserEmbed(currentUser));
            customer.setCode(codes.get(index));
            customer.setDistributor(new CategoryEmbed(distributor));

            customer.setName((String) row.getDatas().get(0));
            customer.setCustomerType(new CategoryEmbed((CustomerType) row.getDatas().get(1)));
            customer.setArea(new CategoryEmbed((Area) row.getDatas().get(2)));
            customer.setMobile((String) row.getDatas().get(3));
            customer.setPhone(row.getDatas().get(4) == null ? null : (String) row.getDatas().get(4));
            customer.setContact(row.getDatas().get(5) == null ? null : (String) row.getDatas().get(5));
            customer.setEmail(row.getDatas().get(6) == null ? null : (String) row.getDatas().get(6));
            customer.setLocation(new double[] { (double) row.getDatas().get(8), (double) row.getDatas().get(7) });

            customersToInsert.add(customer);

            index++;
        }

        if (!customersToInsert.isEmpty()) {
            customerRepository.insertCustomers(userLogin.getClientId(), customersToInsert);
        }

        return new ImportResultDto(dto.getTotal(), customersToInsert.size());
    }

    private Map<String, CustomerType> getCustomerTypeMap(ObjectId clientId) {
        List<CustomerType> customerTypes = customerTypeRepository.getAll(clientId, null);

        HashMap<String, CustomerType> map = new HashMap<String, CustomerType>();

        if (customerTypes != null) {
            for (CustomerType customerType : customerTypes) {
                map.put(customerType.getName().toUpperCase(), customerType);
            }
        }

        return map;
    }

    private Map<String, Area> getAreaMap(ObjectId clientId) {
        List<Area> areas = areaRepository.getAll(clientId, null);

        HashMap<String, Area> map = new HashMap<String, Area>();

        if (areas != null) {
            for (Area area : areas) {
                map.put(area.getName().toUpperCase(), area);
            }
        }

        return map;
    }

}
