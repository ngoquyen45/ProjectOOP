package com.viettel.backend.service.report.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.viettel.backend.domain.Customer;
import com.viettel.backend.domain.Data;
import com.viettel.backend.domain.Distributor;
import com.viettel.backend.domain.Order;
import com.viettel.backend.domain.Product;
import com.viettel.backend.domain.ProductCategory;
import com.viettel.backend.domain.Route;
import com.viettel.backend.domain.Target;
import com.viettel.backend.domain.User;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.domain.Visit;
import com.viettel.backend.domain.embed.OrderDetail;
import com.viettel.backend.dto.common.I_ProductPhotoFactory;
import com.viettel.backend.dto.report.PerformanceDto;
import com.viettel.backend.dto.report.PerformanceDto.ProductCategorySalesQuantityDto;
import com.viettel.backend.dto.report.PerformanceDto.ProductSalesQuantityDto;
import com.viettel.backend.dto.report.SalesResultDailyDto;
import com.viettel.backend.dto.report.SalesResultDto;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.CalendarConfigRepository;
import com.viettel.backend.repository.CustomerPendingRepository;
import com.viettel.backend.repository.DataRepository;
import com.viettel.backend.repository.ProductCategoryRepository;
import com.viettel.backend.repository.ProductRepository;
import com.viettel.backend.repository.RouteRepository;
import com.viettel.backend.repository.ScheduleRepository;
import com.viettel.backend.repository.TargetRepository;
import com.viettel.backend.repository.UserRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.common.AbstractService;
import com.viettel.backend.service.report.PerformanceService;
import com.viettel.backend.util.DateTimeUtils;
import com.viettel.backend.util.entity.SimpleDate;
import com.viettel.backend.util.entity.SimpleDate.Period;

@RolePermission(value = { Role.ADMIN, Role.OBSERVER, Role.SUPERVISOR, Role.DISTRIBUTOR })
@Service
public class PerformanceServiceImpl extends AbstractService implements PerformanceService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CalendarConfigRepository calendarConfigRepository;

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private CustomerPendingRepository customerPendingRepository;

    @Autowired
    private DataRepository dataRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;
    
    @Autowired
    private TargetRepository targetRepository;

    @Override
    public PerformanceDto getPerformanceBySalesman(UserLogin userLogin, String salesmanId, int month, int year) {
        User salesman = getMandatoryPO(userLogin, salesmanId, userRepository);
        BusinessAssert.isTrue(checkSalesmanAccessible(userLogin, salesman.getId()), "salesman not accessible");

        Collection<ObjectId> salesmanIds = Collections.singleton(salesman.getId());
        ObjectId distributorId = salesman.getDistributor().getId();
        Collection<ObjectId> distributorIds = Collections.singleton(distributorId);
        Period period = DateTimeUtils.getPeriodByMonth(month, year);

        List<SimpleDate> workingDays = calendarConfigRepository.getWorkingDays(userLogin.getClientId(), period);
        List<SimpleDate> dates = period.getDates();

        PerformanceDto dto = new PerformanceDto(salesman);
        dto.setNbDay(workingDays.size());

        List<Data> datas = dataRepository.getDatasByCreated(userLogin.getClientId(), salesmanIds, period, null);

        HashMap<String, SalesResultDailyDto> salesResultByDay = new HashMap<>();

        I_ProductPhotoFactory productPhotoFactory = getProductPhotoFactory(userLogin);
        HashMap<ObjectId, ProductSalesQuantityDto> productSalesQuantityMap = new HashMap<>();

        // CALCULATE NB VISIT PLANNED
        List<Route> routes = routeRepository.getRoutesBySalesmen(userLogin.getClientId(), salesmanIds);
        Map<ObjectId, Integer> nbVisitPlannedThisMonthMap = scheduleRepository
                .getNbVisitPlannedByRoute(userLogin.getClientId(), distributorIds, period);
        int nbVisitPlannedThisMonth = 0;
        for (Route route : routes) {
            Integer temp = nbVisitPlannedThisMonthMap.get(route.getId());
            temp = temp == null ? 0 : temp;
            nbVisitPlannedThisMonth += temp;
        }
        dto.getNbVisit().setPlan(nbVisitPlannedThisMonth);

        Target target = targetRepository.getTargetBySalesman(userLogin.getClientId(), salesman.getId(), month, year);
        if (target != null) {
            dto.getRevenue().setPlan(target.getRevenue());
            dto.getProductivity().setPlan(target.getProductivity());
            dto.getQuantity().setPlan(target.getQuantity());
            dto.getNbOrder().setPlan(target.getNbOrder());
            dto.setRevenueByOrderPlan(target.getRevenueByOrder());
            dto.setSkuByOrderPlan(target.getSkuByOrder());
            dto.setQuantityByOrderPlan(target.getQuantityByOrder());
            dto.getNewCustomer().setPlan(target.getNewCustomer());
        }
        
        // DATA
        if (datas != null && !datas.isEmpty()) {
            for (Data data : datas) {
                if (data.isOrder() && data.getApproveStatus() == Order.APPROVE_STATUS_APPROVED) {
                    Order order = data;
                    dto.getNbOrder().incrementActual(1);
                    dto.getRevenue().incrementActual(order.getGrandTotal());
                    dto.getProductivity().incrementActual(order.getProductivity());
                    dto.getQuantity().incrementActual(order.getQuantity());
                    dto.incrementTotalSku(order.getDetails().size());
                    
                    String isoDate = order.getCreatedTime().getIsoDate();
                    SalesResultDailyDto salesResult = salesResultByDay.get(isoDate);
                    if (salesResult == null) {
                        salesResult = new SalesResultDailyDto(isoDate, new SalesResultDto());
                        salesResultByDay.put(isoDate, salesResult);
                    }
    
                    salesResult.getSalesResult().incrementRevenue(order.getGrandTotal());
                    salesResult.getSalesResult().incrementProductivity(order.getProductivity());
                    salesResult.getSalesResult().incrementNbOrder(1);
    
                    for (OrderDetail orderDetail : order.getDetails()) {
                        ObjectId productId = orderDetail.getProduct().getId();
    
                        ProductSalesQuantityDto productSalesQuantity = productSalesQuantityMap.get(productId);
                        if (productSalesQuantity == null) {
                            productSalesQuantity = new ProductSalesQuantityDto(orderDetail.getProduct(),
                                    productPhotoFactory);
                            productSalesQuantityMap.put(productId, productSalesQuantity);
                        }
    
                        productSalesQuantity.incrementQuantity(orderDetail.getQuantity());
                    }
                }
    
                if (data.isVisit() && data.getVisitStatus() == Visit.VISIT_STATUS_VISITED) {
                    Visit visit = data;
    
                    dto.getNbVisit().incrementActual(1);
                    
                    if (visit.getLocationStatus() != Visit.LOCATION_STATUS_LOCATED) {
                        dto.setNbVisitErrorPosition(dto.getNbVisitErrorPosition() + 1);
                    }
                    if (visit.isErrorDuration()) {
                        dto.setNbVisitErrorDuration(dto.getNbVisitErrorDuration() + 1);
                    }
                    if (visit.isOrder()) {
                        dto.setNbVisitHasOrder(dto.getNbVisitHasOrder() + 1);
                    }
                }
            }
        }

        // PRODUCT
        List<Product> products = productRepository.getAll(userLogin.getClientId(), null);
        HashMap<ObjectId, ProductCategorySalesQuantityDto> productCategorySalesQuantityMap = new HashMap<>();
        for (Product product : products) {
            ObjectId productCategoryId = product.getProductCategory().getId();

            ProductCategorySalesQuantityDto productCategorySalesQuantity = productCategorySalesQuantityMap
                    .get(productCategoryId);
            if (productCategorySalesQuantity == null) {
                productCategorySalesQuantity = new ProductCategorySalesQuantityDto(product.getProductCategory());
                productCategorySalesQuantityMap.put(productCategoryId, productCategorySalesQuantity);
            }

            ProductSalesQuantityDto productSalesQuantity = productSalesQuantityMap.get(product.getId());
            if (productSalesQuantity == null) {
                productCategorySalesQuantity
                        .addProductsSalesQuantity(new ProductSalesQuantityDto(product, productPhotoFactory));
            } else {
                productCategorySalesQuantity.addProductsSalesQuantity(productSalesQuantity);
                productCategorySalesQuantity.incrementQuantity(productSalesQuantity.getQuantity());
            }
        }
        List<ProductCategory> productCategories = productCategoryRepository.getAll(userLogin.getClientId(), null);
        List<ProductCategorySalesQuantityDto> productCategoriesSalesQuantity = new ArrayList<>(productCategories.size());
        for (ProductCategory productCategory : productCategories) {
            ProductCategorySalesQuantityDto temp = productCategorySalesQuantityMap.get(productCategory.getId());
            if (temp == null) {
                temp = new ProductCategorySalesQuantityDto(productCategory);
            }
            productCategoriesSalesQuantity.add(temp);
        }
        dto.setProductCategoriesSalesQuantity(productCategoriesSalesQuantity);

        // DAILY
        List<SalesResultDailyDto> salesResultsDaily = new ArrayList<>(dates.size());
        for (SimpleDate date : dates) {
            String isoDate = date.getIsoDate();
            SalesResultDailyDto salesResult = salesResultByDay.get(isoDate);
            if (salesResult == null) {
                salesResult = new SalesResultDailyDto(isoDate, new SalesResultDto());
            }
            salesResultsDaily.add(salesResult);
        }
        dto.setSalesResultsDaily(salesResultsDaily);

        // NEW CUSTOMER
        long newCustomer = customerPendingRepository.countCustomersByCreatedUsers(userLogin.getClientId(), salesmanIds,
                Customer.APPROVE_STATUS_APPROVED, null);
        dto.getNewCustomer().incrementActual((int) newCustomer);

        return dto;
    }

    private boolean checkSalesmanAccessible(UserLogin userLogin, ObjectId salesmanId) {
        Assert.notNull(salesmanId);

        List<Distributor> distributors = getAccessibleDistributors(userLogin);
        Set<ObjectId> distributorIds = getIdSet(distributors);

        List<User> salesmen = userRepository.getSalesmenByDistributors(userLogin.getClientId(), distributorIds);
        Set<ObjectId> salesmanIds = getIdSet(salesmen);

        return salesmanIds.contains(salesmanId);
    }

}
