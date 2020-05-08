package com.viettel.backend.service.report.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viettel.backend.domain.Config;
import com.viettel.backend.domain.Config.OrderDateType;
import com.viettel.backend.domain.Distributor;
import com.viettel.backend.domain.Order;
import com.viettel.backend.domain.Product;
import com.viettel.backend.domain.ProductCategory;
import com.viettel.backend.domain.User;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.domain.embed.OrderDetail;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.report.DistributorSalesResultDto;
import com.viettel.backend.dto.report.ProductSalesResultDto;
import com.viettel.backend.dto.report.SalesResultDailyDto;
import com.viettel.backend.dto.report.SalesResultDto;
import com.viettel.backend.dto.report.SalesmanSalesResultDto;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.OrderRepository;
import com.viettel.backend.repository.ProductCategoryRepository;
import com.viettel.backend.repository.ProductRepository;
import com.viettel.backend.repository.UserRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.common.AbstractService;
import com.viettel.backend.service.report.SalesReportService;
import com.viettel.backend.util.DateTimeUtils;
import com.viettel.backend.util.entity.SimpleDate;
import com.viettel.backend.util.entity.SimpleDate.Period;

@RolePermission(value = { Role.ADMIN, Role.OBSERVER, Role.SUPERVISOR, Role.DISTRIBUTOR })
@Service
public class SalesReportServiceImpl extends AbstractService implements SalesReportService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Override
    public ListDto<SalesResultDailyDto> getSalesResultDaily(UserLogin userLogin, int month, int year) {
        List<Distributor> distributors = getAccessibleDistributors(userLogin);
        Set<ObjectId> distributorIds = getIdSet(distributors);

        Config config = getConfig(userLogin);

        Period period = DateTimeUtils.getPeriodByMonth(month, year);

        List<Order> orders = orderRepository.getOrdersByDistributors(userLogin.getClientId(), distributorIds, period,
                config.getOrderDateType(), null, null);

        HashMap<String, SalesResultDailyDto> reportByDate = new HashMap<String, SalesResultDailyDto>();
        HashMap<String, HashSet<ObjectId>> distributorIdsByDate = new HashMap<String, HashSet<ObjectId>>();
        HashMap<String, HashSet<ObjectId>> customerIdsByDate = new HashMap<String, HashSet<ObjectId>>();
        HashMap<String, HashSet<ObjectId>> salesmanIdsByDate = new HashMap<String, HashSet<ObjectId>>();
        if (orders != null && !orders.isEmpty()) {
            for (Order order : orders) {
                String isoDate = null;
                if (config.getOrderDateType() == OrderDateType.CREATED_DATE) {
                    isoDate = order.getCreatedTime().getIsoDate();
                } else {
                    isoDate = order.getApproveTime().getIsoDate();
                }

                SalesResultDailyDto report = reportByDate.get(isoDate);
                if (report == null) {
                    report = new SalesResultDailyDto(isoDate, new SalesResultDto());
                    reportByDate.put(isoDate, report);
                }

                HashSet<ObjectId> distributorIdsTemp = distributorIdsByDate.get(isoDate);
                if (distributorIdsTemp == null) {
                    distributorIdsTemp = new HashSet<ObjectId>();
                    distributorIdsByDate.put(isoDate, distributorIdsTemp);
                }
                distributorIdsTemp.add(order.getDistributor().getId());

                HashSet<ObjectId> customerIds = customerIdsByDate.get(isoDate);
                if (customerIds == null) {
                    customerIds = new HashSet<ObjectId>();
                    customerIdsByDate.put(isoDate, customerIds);
                }
                customerIds.add(order.getCustomer().getId());

                HashSet<ObjectId> salesmanIds = salesmanIdsByDate.get(isoDate);
                if (salesmanIds == null) {
                    salesmanIds = new HashSet<ObjectId>();
                    salesmanIdsByDate.put(isoDate, salesmanIds);
                }
                salesmanIds.add(order.getCreatedBy().getId());

                report.getSalesResult().incrementRevenue(order.getGrandTotal());
                report.getSalesResult().incrementProductivity(order.getProductivity());
                report.getSalesResult().incrementNbOrder(1);
                report.getSalesResult().incrementNbDistributor(distributorIdsTemp.size());
                report.getSalesResult().incrementNbCustomer(customerIds.size());
                report.getSalesResult().incrementNbSalesman(salesmanIds.size());
            }
        }

        List<SalesResultDailyDto> dtos = new LinkedList<SalesResultDailyDto>();
        SimpleDate tempDate = period.getFromDate();
        while (tempDate.compareTo(period.getToDate()) < 0) {
            String isoDate = tempDate.getIsoDate();
            SalesResultDailyDto report = reportByDate.get(isoDate);
            if (report == null) {
                report = new SalesResultDailyDto(isoDate, new SalesResultDto());
            }
            dtos.add(report);

            tempDate = DateTimeUtils.addDays(tempDate, 1);
        }

        return new ListDto<SalesResultDailyDto>(dtos);
    }

    @Override
    public ListDto<DistributorSalesResultDto> getDistributorSalesResult(UserLogin userLogin, int month, int year) {
        List<Distributor> distributors = getAccessibleDistributors(userLogin);
        Set<ObjectId> distributorIds = getIdSet(distributors);

        Config config = getConfig(userLogin);

        Period period = DateTimeUtils.getPeriodByMonth(month, year);

        List<Order> orders = orderRepository.getOrdersByDistributors(userLogin.getClientId(), distributorIds, period,
                config.getOrderDateType(), null, null);

        HashMap<ObjectId, DistributorSalesResultDto> reportByDistributor = new HashMap<ObjectId, DistributorSalesResultDto>();
        HashMap<ObjectId, HashSet<ObjectId>> customerIdsByDistributor = new HashMap<ObjectId, HashSet<ObjectId>>();
        HashMap<ObjectId, HashSet<ObjectId>> salesmanIdsByDistributor = new HashMap<ObjectId, HashSet<ObjectId>>();

        if (orders != null && !orders.isEmpty()) {
            for (Order order : orders) {
                DistributorSalesResultDto report = reportByDistributor.get(order.getDistributor().getId());
                if (report == null) {
                    report = new DistributorSalesResultDto(order.getDistributor(), new SalesResultDto());
                    reportByDistributor.put(order.getDistributor().getId(), report);
                }

                HashSet<ObjectId> customerIds = customerIdsByDistributor.get(order.getDistributor().getId());
                if (customerIds == null) {
                    customerIds = new HashSet<ObjectId>();
                    customerIdsByDistributor.put(order.getDistributor().getId(), customerIds);
                }
                customerIds.add(order.getCustomer().getId());

                HashSet<ObjectId> salesmanIds = salesmanIdsByDistributor.get(order.getDistributor().getId());
                if (salesmanIds == null) {
                    salesmanIds = new HashSet<ObjectId>();
                    salesmanIdsByDistributor.put(order.getDistributor().getId(), salesmanIds);
                }
                salesmanIds.add(order.getCreatedBy().getId());

                report.getSalesResult().incrementRevenue(order.getGrandTotal());
                report.getSalesResult().incrementProductivity(order.getProductivity());
                report.getSalesResult().incrementNbOrder(1);
                report.getSalesResult().incrementNbCustomer(customerIds.size());
                report.getSalesResult().incrementNbSalesman(salesmanIds.size());
            }
        }

        List<DistributorSalesResultDto> dtos = new ArrayList<DistributorSalesResultDto>(distributors.size());
        for (Distributor distributor : distributors) {
            DistributorSalesResultDto report = reportByDistributor.get(distributor.getId());
            if (report == null) {
                report = new DistributorSalesResultDto(distributor, new SalesResultDto());
            }
            dtos.add(report);
        }

        return new ListDto<DistributorSalesResultDto>(dtos);
    }

    @Override
    public ListDto<ProductSalesResultDto> getProductSalesResult(UserLogin userLogin, int month, int year,
            String productCategoryId) {
        List<Distributor> distributors = getAccessibleDistributors(userLogin);
        Set<ObjectId> distributorIds = getIdSet(distributors);

        Config config = getConfig(userLogin);

        Period period = DateTimeUtils.getPeriodByMonth(month, year);

        List<Order> orders = orderRepository.getOrdersByDistributors(userLogin.getClientId(), distributorIds, period,
                config.getOrderDateType(), null, null);

        if (orders == null || orders.isEmpty()) {
            return ListDto.emptyList();
        }
        
        ProductCategory productCategory = getMandatoryPO(userLogin, productCategoryId, productCategoryRepository);

        List<Product> products = productRepository.getProductsByCategories(userLogin.getClientId(),
                Collections.singleton(productCategory.getId()));
        if (products == null || products.isEmpty()) {
            return ListDto.emptyList();
        }
        Set<ObjectId> productIds = new HashSet<ObjectId>();
        for (Product product : products) {
            productIds.add(product.getId());
        }

        HashMap<ObjectId, ProductSalesResultDto> reportByProduct = new HashMap<ObjectId, ProductSalesResultDto>();
        for (Order order : orders) {
            for (OrderDetail orderDetail : order.getDetails()) {
                if (!productIds.contains(orderDetail.getProduct().getId())) {
                    continue;
                }

                ProductSalesResultDto report = reportByProduct.get(orderDetail.getProduct().getId());
                if (report == null) {
                    report = new ProductSalesResultDto(orderDetail.getProduct(), new SalesResultDto());
                    reportByProduct.put(orderDetail.getProduct().getId(), report);
                }

                report.getSalesResult().incrementRevenue(order.getGrandTotal());
                report.getSalesResult().incrementProductivity(order.getProductivity());
                report.getSalesResult().incrementNbOrder(1);
            }
        }

        List<ProductSalesResultDto> dtos = new ArrayList<ProductSalesResultDto>(products.size());
        for (Product product : products) {
            ProductSalesResultDto report = reportByProduct.get(product.getId());
            if (report == null) {
                report = new ProductSalesResultDto(product, new SalesResultDto());
            }
            dtos.add(report);
        }

        return new ListDto<ProductSalesResultDto>(dtos);
    }

    @Override
    public ListDto<SalesmanSalesResultDto> getSalesmanSalesResult(UserLogin userLogin, int month, int year) {
        List<Distributor> distributors = getAccessibleDistributors(userLogin);
        Set<ObjectId> distributorIds = getIdSet(distributors);

        Config config = getConfig(userLogin);

        Period period = DateTimeUtils.getPeriodByMonth(month, year);

        List<Order> orders = orderRepository.getOrdersByDistributors(userLogin.getClientId(), distributorIds, period,
                config.getOrderDateType(), null, null);

        HashMap<ObjectId, SalesmanSalesResultDto> reportBySalesman = new HashMap<ObjectId, SalesmanSalesResultDto>();
        if (orders != null && !orders.isEmpty()) {
            for (Order order : orders) {
                SalesmanSalesResultDto report = reportBySalesman.get(order.getCreatedBy().getId());
                if (report == null) {
                    report = new SalesmanSalesResultDto(order.getCreatedBy(), new SalesResultDto());
                    reportBySalesman.put(order.getCreatedBy().getId(), report);
                }

                report.getSalesResult().incrementRevenue(order.getGrandTotal());
                report.getSalesResult().incrementProductivity(order.getProductivity());
                report.getSalesResult().incrementNbOrder(1);
            }
        }

        List<User> salesmen = userRepository.getSalesmenByDistributors(userLogin.getClientId(), distributorIds);
        List<SalesmanSalesResultDto> dtos = new LinkedList<SalesmanSalesResultDto>();
        for (User salesman : salesmen) {
            SalesmanSalesResultDto report = reportBySalesman.get(salesman.getId());
            if (report == null) {
                report = new SalesmanSalesResultDto(salesman, new SalesResultDto());
            }
            dtos.add(report);
            
            reportBySalesman.remove(salesman.getId());
        }
        
        // ADD NHUNG SALESMAN MA CO ORDER TRONG THANG TUY NHIEN KHONG CON THUOC DISTRIBUTOR NAY NUA
        for (SalesmanSalesResultDto report : reportBySalesman.values()) {
            dtos.add(report);
        }

        return new ListDto<SalesmanSalesResultDto>(dtos);
    }

}
