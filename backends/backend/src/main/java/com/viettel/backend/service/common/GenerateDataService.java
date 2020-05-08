package com.viettel.backend.service.common;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.viettel.backend.domain.Client;
import com.viettel.backend.domain.Config;
import com.viettel.backend.domain.Customer;
import com.viettel.backend.domain.Distributor;
import com.viettel.backend.domain.Order;
import com.viettel.backend.domain.Product;
import com.viettel.backend.domain.Route;
import com.viettel.backend.domain.User;
import com.viettel.backend.domain.Visit;
import com.viettel.backend.domain.embed.CategoryEmbed;
import com.viettel.backend.domain.embed.CustomerEmbed;
import com.viettel.backend.domain.embed.UserEmbed;
import com.viettel.backend.dto.order.OrderCreateDto;
import com.viettel.backend.dto.order.OrderCreateDto.OrderDetailCreatedDto;
import com.viettel.backend.engine.file.DbFileMeta;
import com.viettel.backend.engine.file.FileEngine;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.CalendarConfigRepository;
import com.viettel.backend.repository.CodeGeneratorRepository;
import com.viettel.backend.repository.ConfigRepository;
import com.viettel.backend.repository.CustomerRepository;
import com.viettel.backend.repository.DistributorRepository;
import com.viettel.backend.repository.MasterDataRepository;
import com.viettel.backend.repository.OrderRepository;
import com.viettel.backend.repository.ProductRepository;
import com.viettel.backend.repository.RouteRepository;
import com.viettel.backend.repository.UserRepository;
import com.viettel.backend.repository.VisitRepository;
import com.viettel.backend.util.DateTimeUtils;
import com.viettel.backend.util.LocationUtils;
import com.viettel.backend.util.entity.Location;
import com.viettel.backend.util.entity.SimpleDate;
import com.viettel.backend.util.entity.SimpleDate.Period;

@Service
public class GenerateDataService extends AbstractService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DistributorRepository distributorRepository;

    @Autowired
    private CalendarConfigRepository calendarConfigRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ConfigRepository configRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderCalculationService orderCalculationService;

    @Autowired
    private CodeGeneratorRepository codeGeneratorRepository;

    @Autowired
    private MasterDataRepository masterDataRepository;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private FileEngine fileEngine;

    public void generateVisitAndOrder(Client client, SimpleDate fromDate, SimpleDate toDate) {
        ObjectId clientId = client.getId();

        masterDataRepository.deleteVisitAndOrder(clientId);
        cacheService.clearByClient(clientId);

        User defaultAdmin = userRepository.getDefaultAdmin(clientId);
        UserLogin userLogin = new UserLogin(clientId, client.getCode(), client.getName(), defaultAdmin.getId(),
                defaultAdmin.getUsername(), defaultAdmin.getRole());

        HashMap<ObjectId, Set<ObjectId>> routeIdsBySalesman = new HashMap<ObjectId, Set<ObjectId>>();

        Config config = configRepository.getConfig(clientId);
        List<Product> products = productRepository.getAll(clientId, null);

        List<SimpleDate> dates = calendarConfigRepository.getWorkingDays(clientId, new Period(fromDate, toDate));

        List<User> supervisors = userRepository.getSupervisors(clientId);
        HashMap<ObjectId, List<Distributor>> distributorBySupervisor = new HashMap<ObjectId, List<Distributor>>();
        HashMap<ObjectId, List<User>> salesmanByDistributor = new HashMap<ObjectId, List<User>>();
        HashMap<ObjectId, List<Customer>> customerBySalesman = new HashMap<ObjectId, List<Customer>>();

        for (User supervisor : supervisors) {
            List<Distributor> distributors = distributorRepository.getDistributorsBySupervisors(clientId,
                    Arrays.asList(supervisor.getId()));
            distributorBySupervisor.put(supervisor.getId(), distributors);

            for (Distributor distributor : distributors) {
                List<User> salesmen = userRepository.getSalesmenByDistributors(clientId,
                        Arrays.asList(distributor.getId()));
                salesmanByDistributor.put(distributor.getId(), salesmen);

                for (User salesman : salesmen) {
                    List<Route> routes = routeRepository.getRoutesBySalesmen(clientId,
                            Collections.singleton(salesman.getId()));
                    Set<ObjectId> routeIds = getIdSet(routes);
                    routeIdsBySalesman.put(salesman.getId(), routeIds);

                    customerBySalesman.put(salesman.getId(),
                            customerRepository.getCustomersByRoutes(clientId, routeIds, null));
                }
            }
        }

        ClassPathResource closePhotoResource = new ClassPathResource("customer-close-photo.jpeg");
        ClassPathResource openPhotoResource = new ClassPathResource("customer-open-photo.jpg");
        String closePhoto = null;
        String openPhoto = null;
        try {
            closePhoto = fileEngine.storeImage(closePhotoResource.getInputStream(), closePhotoResource.getFilename(),
                    "image/jpeg", new DbFileMeta(), null);
            
            openPhoto = fileEngine.storeImage(openPhotoResource.getInputStream(), openPhotoResource.getFilename(),
                    "image/jpeg", new DbFileMeta(), null);
        } catch (IOException e) {
            e.printStackTrace();
            throw new UnsupportedOperationException();
        }

        Random random = new Random();

        ExecutorService es = Executors.newCachedThreadPool();
        for (SimpleDate currentDate : dates) {
            for (User supervisor : supervisors) {
                List<Distributor> distributors = distributorBySupervisor.get(supervisor.getId());
                for (Distributor distributor : distributors) {
                    List<User> salesmen = salesmanByDistributor.get(distributor.getId());
                    for (User salesman : salesmen) {

                        SalesmanDataGenerate salesmanDataGenerate = new SalesmanDataGenerate(userLogin, random, config, currentDate,
                                supervisor, distributor, salesman, products, customerBySalesman, routeIdsBySalesman, openPhoto,
                                closePhoto);

                        es.execute(salesmanDataGenerate);
                    }
                }
            }
        }

        try {
            es.shutdown();
            es.awaitTermination(10, TimeUnit.MINUTES);
            cacheService.initBusinessCache(clientId);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new UnsupportedOperationException();
        }
    }

    // PRIVATE
    private Visit createVisit(UserLogin userLogin, Config config, User salesman, Distributor distributor,
            Customer customer, Location location) {
        Visit visit = new Visit();

        visit.setClientId(userLogin.getClientId());
        visit.setActive(true);
        visit.setDraft(false);

        // LOCATION STATUS
        if (!LocationUtils.checkLocationValid(customer.getLocation())) {
            visit.setLocationStatus(Visit.LOCATION_STATUS_CUSTOMER_UNLOCATED);
            visit.setLocation(LocationUtils.convert(location));
            visit.setCustomerLocation(null);
        } else {
            if (!LocationUtils.checkLocationValid(location)) {
                visit.setLocationStatus(Visit.LOCATION_STATUS_UNLOCATED);
                visit.setLocation(null);
                visit.setCustomerLocation(customer.getLocation());
            } else {
                double distance = LocationUtils.calculateDistance(customer.getLocation()[1], customer.getLocation()[0],
                        location.getLatitude(), location.getLongitude());
                if (distance > config.getVisitDistanceKPI()) {
                    visit.setLocationStatus(Visit.LOCATION_STATUS_TOO_FAR);
                } else {
                    visit.setLocationStatus(Visit.LOCATION_STATUS_LOCATED);
                }

                visit.setLocation(LocationUtils.convert(location));
                visit.setCustomerLocation(customer.getLocation());
                visit.setDistance(distance);
            }
        }

        visit.setDistributor(new CategoryEmbed(distributor));
        visit.setSalesman(new UserEmbed(salesman));
        visit.setCustomer(new CustomerEmbed(customer));

        return visit;
    }

    /** distance in km */
    private double[] generateLocation(double longitude, double latitude, double minDistance, double maxDistance) {
        Random random = new Random();

        double distance = -1;

        double foundLongitude = -1;
        double foundLatitude = -1;

        while (distance <= minDistance || distance >= maxDistance) {
            // Convert radius from meters to degrees
            double radiusInDegrees = (maxDistance * 1000) / 111000f;

            double u = random.nextDouble();
            double v = random.nextDouble();
            double w = radiusInDegrees * Math.sqrt(u);
            double t = 2 * Math.PI * v;
            double x = w * Math.cos(t);
            double y = w * Math.sin(t);

            // Adjust the x-coordinate for the shrinking of the east-west
            // distances
            double new_x = x / Math.cos(latitude);

            foundLongitude = new_x + longitude;
            foundLatitude = y + latitude;

            distance = LocationUtils.calculateDistance(latitude, longitude, foundLatitude, foundLongitude);
        }

        return new double[] { foundLongitude, foundLatitude };
    }

    private OrderCreateDto getRandomOrder(List<Product> products) {
        OrderCreateDto order = new OrderCreateDto();
        order.setDiscountAmt(BigDecimal.ZERO);
        List<OrderDetailCreatedDto> details = new LinkedList<OrderDetailCreatedDto>();
        Random random = new Random();

        double nbProduct = products.size();

        int nbProductOfOrder = 2 + ((int) (random.nextDouble() * 3.0));
        List<Integer> productIndexs = new ArrayList<Integer>(nbProductOfOrder);

        for (int i = 0; i < nbProductOfOrder; i++) {
            int index = (int) (random.nextDouble() * nbProduct);
            while (productIndexs.contains(index) || index < 0 || index >= products.size()) {
                index = (int) (random.nextDouble() * nbProduct);
            }
            productIndexs.add(index);
        }

        for (Integer productIndex : productIndexs) {
            int quantity = 3 + ((int) (random.nextDouble() * 4.0));
            details.add(createOrderDetail(products.get(productIndex).getId().toString(), quantity));
        }

        order.setDetails(details);

        return order;
    }

    private OrderDetailCreatedDto createOrderDetail(String productId, int quantity) {
        OrderDetailCreatedDto dto = new OrderDetailCreatedDto();
        dto.setProductId(productId);
        dto.setQuantity(new BigDecimal(quantity));

        return dto;
    }

    private class SalesmanDataGenerate implements Runnable {

        private UserLogin userLogin;
        private Random random;
        private Config config;
        private SimpleDate currentDate;
        private User supervisor;
        private Distributor distributor;
        private User salesman;
        private List<Product> products;
        private Map<ObjectId, List<Customer>> customerBySalesman = new HashMap<ObjectId, List<Customer>>();
        private Map<ObjectId, Set<ObjectId>> routeIdsBySalesman;
        private String openPhoto;
        private String closePhoto;

        public SalesmanDataGenerate(UserLogin userLogin, Random random, Config config, SimpleDate currentDate,
                User supervisor, Distributor distributor, User salesman, List<Product> products,
                Map<ObjectId, List<Customer>> customerBySalesman, Map<ObjectId, Set<ObjectId>> routeIdsBySalesman,
                String openPhoto, String closePhoto) {
            super();
            this.userLogin = userLogin;
            this.random = random;
            this.config = config;
            this.currentDate = currentDate;
            this.supervisor = supervisor;
            this.distributor = distributor;
            this.salesman = salesman;
            this.products = products;
            this.customerBySalesman = customerBySalesman;
            this.routeIdsBySalesman = routeIdsBySalesman;
            this.openPhoto = openPhoto;
            this.closePhoto = closePhoto;
        }

        @Override
        public void run() {
            System.out.println(currentDate.getIsoDate() + " " + salesman.getFullname() + "start");
            
            ObjectId clientId = userLogin.getClientId();
            Set<ObjectId> routeIds = routeIdsBySalesman.get(salesman.getId());
            List<Customer> customers = customerRepository.getCustomersByRoutes(clientId, routeIds, currentDate);

            SimpleDate time = new SimpleDate(currentDate);
            time.setHour(8);

            Set<ObjectId> customeIds = new HashSet<ObjectId>();
            for (Customer customer : customers) {
                customeIds.add(customer.getId());

                // 90% visit
                if (random.nextDouble() < 0.6) {
                    Location location = null;
                    // 90% xac dinh vi tri
                    if (random.nextDouble() < 0.9) {

                        // 10% far
                        if (random.nextDouble() < 0.3) {
                            double[] l = generateLocation(customer.getLocation()[0], customer.getLocation()[1],
                                    config.getVisitDistanceKPI(), config.getVisitDistanceKPI() * 2);
                            location = new Location(l);
                        } else {
                            double[] l = generateLocation(customer.getLocation()[0], customer.getLocation()[1], 0.0,
                                    config.getVisitDistanceKPI());
                            location = new Location(l);
                        }

                    }

                    Visit visit = createVisit(userLogin, config, salesman, distributor, customer, location);

                    // 10% dong cua
                    if (random.nextDouble() < 0.1) {
                        visit.setClosed(true);
                        visit.setStartTime(time);
                        visit.setEndTime(time);
                        visit.setDuration(0);
                        visit.setErrorDuration(false);
                        visit.setPhoto(closePhoto);
                        visit.setVisitStatus(Visit.VISIT_STATUS_VISITED);
                    } else {
                        visit.setClosed(false);
                        visit.setStartTime(time);
                        // from 3 min -> 10 min
                        double duration = 3 + (7 * random.nextDouble());
                        time = DateTimeUtils.addMinutes(time, (int) duration);
                        visit.setEndTime(time);

                        visit.setDuration(SimpleDate.getDuration(visit.getStartTime(), visit.getEndTime()));
                        visit.setErrorDuration(visit.getDuration() < config.getVisitDurationKPI());

                        visit.setVisitStatus(Visit.VISIT_STATUS_VISITED);

                        // 20 co photo
                        if (random.nextDouble() < 0.2) {
                            visit.setPhoto(openPhoto);
                        }
                        
                        // 80% co order
                        if (random.nextDouble() < 0.8) {
                            OrderCreateDto orderDto = getRandomOrder(products);
                            visit.setCode(
                                    codeGeneratorRepository.getOrderCode(clientId.toString(), visit.getCreatedTime()));
                            visit.setDeliveryType(Order.DELIVERY_TYPE_IMMEDIATE);

                            orderCalculationService.calculate(userLogin, orderDto, visit, null);

                            visit.setApproveStatus(Order.APPROVE_STATUS_APPROVED);
                            visit.setApproveTime(time);
                            
                            // 30% Van Sales
                            if (random.nextDouble() < 0.3) { 
                                visit.setApproveUser(new UserEmbed(salesman));
                                visit.setVanSales(true);
                            } else {
                                visit.setApproveUser(new UserEmbed(supervisor));
                                visit.setVanSales(false);
                            }
                        }
                    }

                    visit = visitRepository.save(clientId, visit);

                    // lan ghe tham tiep theo sau 10 phut
                    time = DateTimeUtils.addMinutes(time, 2);
                }
            }

            int numberOrderUnplanned = (int) (3.0 * random.nextDouble());
            for (int i = 0; i < numberOrderUnplanned; i++) {
                List<Customer> allCustomers = customerBySalesman.get(salesman.getId());
                Customer customer = null;
                for (Customer c : allCustomers) {
                    if (!customeIds.contains(c.getId())) {
                        customer = c;
                        break;
                    }
                }

                if (customer == null) {
                    break;
                }

                Order order = new Order();
                order.setClientId(clientId);
                order.setActive(true);
                order.setDraft(false);
                order.setCreatedBy(new UserEmbed(salesman));
                order.setCustomer(new CustomerEmbed(customer));
                order.setDistributor(new CategoryEmbed(distributor));
                order.setCreatedTime(time);
                order.setCode(codeGeneratorRepository.getOrderCode(clientId.toString(), order.getCreatedTime()));

                order.setApproveStatus(Order.APPROVE_STATUS_APPROVED);
                order.setApproveTime(time);
                order.setApproveUser(new UserEmbed(supervisor));

                order.setDeliveryType(Order.DELIVERY_TYPE_IMMEDIATE);

                orderCalculationService.calculate(userLogin, getRandomOrder(products), order, null);

                order = orderRepository.save(userLogin.getClientId(), order);

                time = DateTimeUtils.addMinutes(time, 10);
            }
            
            System.out.println(currentDate.getIsoDate() + " " + salesman.getFullname() + "end");
        }
    }
}
