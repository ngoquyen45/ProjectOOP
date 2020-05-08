package com.viettel.backend.service.report.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viettel.backend.domain.Config;
import com.viettel.backend.domain.Customer;
import com.viettel.backend.domain.Distributor;
import com.viettel.backend.domain.Order;
import com.viettel.backend.domain.Route;
import com.viettel.backend.domain.Target;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.domain.embed.CustomerEmbed;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.common.ProgressDto;
import com.viettel.backend.dto.order.OrderSimpleDto;
import com.viettel.backend.dto.report.CustomerSalesResultDto;
import com.viettel.backend.dto.report.MobileDashboardDto;
import com.viettel.backend.dto.report.SalesResultDailyDto;
import com.viettel.backend.dto.report.SalesResultDto;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.CalendarConfigRepository;
import com.viettel.backend.repository.CustomerRepository;
import com.viettel.backend.repository.OrderRepository;
import com.viettel.backend.repository.RouteRepository;
import com.viettel.backend.repository.ScheduleRepository;
import com.viettel.backend.repository.TargetRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.common.AbstractService;
import com.viettel.backend.service.common.CacheService;
import com.viettel.backend.service.common.CacheService.DateType;
import com.viettel.backend.service.report.MobileDashboardService;
import com.viettel.backend.util.DateTimeUtils;
import com.viettel.backend.util.entity.IncrementHashMap;
import com.viettel.backend.util.entity.SimpleDate;
import com.viettel.backend.util.entity.SimpleDate.Period;

@RolePermission(value = { Role.SALESMAN })
@Service
public class MobileDashboardServiceImpl extends AbstractService implements MobileDashboardService {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private CalendarConfigRepository calendarConfigRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TargetRepository targetRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;
    
    @Autowired
    private RouteRepository routeRepository;
    
    private Set<ObjectId> getSalesmanIds(UserLogin userLogin) {
        return Collections.singleton(userLogin.getUserId());
    }

    @Override
    public MobileDashboardDto getMobileDashboard(UserLogin userLogin) {
        SimpleDate today = DateTimeUtils.getToday();

        List<SimpleDate> workingDaysAllMonth = calendarConfigRepository.getWorkingDays(userLogin.getClientId(),
                DateTimeUtils.getPeriodThisMonth());

        List<SimpleDate> workingDaysUntilToday = calendarConfigRepository.getWorkingDays(userLogin.getClientId(),
                DateTimeUtils.getPeriodThisMonthUntilToday());

        Set<ObjectId> salesmanIds = getSalesmanIds(userLogin);
        
        Distributor distributor = getDefaultDistributor(userLogin);

        double revenue = cacheService.getRevenueByCreated(userLogin.getClientId(), DateType.MONTHLY, today,
                distributor.getId(), salesmanIds);

        double productitvity = cacheService.getProductivityByCreated(userLogin.getClientId(), DateType.MONTHLY, today,
                distributor.getId(), salesmanIds);

        int nbVisit = (int) cacheService.getNbVisitBySalesman(userLogin.getClientId(), DateType.MONTHLY, today,
                distributor.getId(), salesmanIds);

        // GET TARGET
        List<Target> targets = targetRepository.getTargetsBySalesmen(userLogin.getClientId(), salesmanIds,
                today.getMonth(), today.getYear(), null);
        BigDecimal revenueTarget = BigDecimal.ZERO;
        if (targets != null && !targets.isEmpty()) {
            for (Target target : targets) {
                revenueTarget = revenueTarget.add(target.getRevenue());
            }
        }

        // VISIT PLANNED
        Map<ObjectId, Integer> nbVisitPlannedByRouteThisMonth = scheduleRepository.getNbVisitPlannedByRoute(
                userLogin.getClientId(), Collections.singleton(distributor.getId()), DateTimeUtils.getPeriodThisMonth());
        List<Route> routes = routeRepository.getRoutesBySalesmen(userLogin.getClientId(), salesmanIds);
        
        Integer nbVisitPlanned = 0;
        for (Route route : routes) {
            Integer tmp = nbVisitPlannedByRouteThisMonth.get(route.getId());
            tmp = tmp == null ? 0 : tmp;
            nbVisitPlanned += tmp;
        }

        
        return new MobileDashboardDto(new ProgressDto(revenueTarget.doubleValue(), revenue),
                new ProgressDto(0.0, productitvity), new ProgressDto(nbVisitPlanned, nbVisit),
                new ProgressDto(workingDaysAllMonth.size(), workingDaysUntilToday.size()));
    }

    @RolePermission(value = { Role.SALESMAN, Role.DISTRIBUTOR })
    @Override
    public ListDto<CustomerSalesResultDto> getCustomerSalesResultsThisMonth(UserLogin userLogin) {
        Period thisMonthPeriod = DateTimeUtils.getPeriodThisMonth();

        Set<ObjectId> salesmanIds = getSalesmanIds(userLogin);

        Config config = getConfig(userLogin);

        List<Order> orders = orderRepository.getOrdersByCreatedUsers(userLogin.getClientId(), salesmanIds,
                thisMonthPeriod, config.getOrderDateType(), null);

        if (orders == null || orders.isEmpty()) {
            return ListDto.emptyList();
        }

        Map<ObjectId, CustomerEmbed> customerById = new HashMap<>();
        IncrementHashMap<ObjectId> revenueByCustomer = new IncrementHashMap<>();
        IncrementHashMap<ObjectId> productivityByCustomer = new IncrementHashMap<>();
        IncrementHashMap<ObjectId> nbOrderByCustomer = new IncrementHashMap<>();

        for (Order order : orders) {
            customerById.put(order.getCustomer().getId(), order.getCustomer());
            revenueByCustomer.increment(order.getCustomer().getId(), order.getGrandTotal());
            productivityByCustomer.increment(order.getCustomer().getId(), order.getProductivity());
            nbOrderByCustomer.increment(order.getCustomer().getId(), 1);
        }

        List<CustomerSalesResultDto> dtos = new ArrayList<>(customerById.size());
        for (CustomerEmbed customer : customerById.values()) {
            BigDecimal revenue = revenueByCustomer.get(customer.getId());
            BigDecimal productivity = productivityByCustomer.get(customer.getId());
            int nbOrder = nbOrderByCustomer.getIntValue(customer.getId());

            CustomerSalesResultDto dto = new CustomerSalesResultDto(customer,
                    new SalesResultDto(revenue, productivity, nbOrder));

            dtos.add(dto);
        }

        return new ListDto<>(dtos);
    }

    @Override
    public ListDto<OrderSimpleDto> getOrderByCustomerThisMonth(UserLogin userLogin, String customerId) {
        Customer customer = getMandatoryPO(userLogin, customerId, false, null, customerRepository);
        // CHECK ONLY DISTRIBUTOR
        BusinessAssert.isTrue(checkAccessible(userLogin, customer.getDistributor().getId()), "customer not accessible");

        Period thisMonthPeriod = DateTimeUtils.getPeriodThisMonth();

        Set<ObjectId> salesmanIds = getSalesmanIds(userLogin);

        Config config = getConfig(userLogin);

        List<Order> orders = orderRepository.getOrdersByCreatedUsers(userLogin.getClientId(), salesmanIds,
                thisMonthPeriod, config.getOrderDateType(), Collections.singleton(customer.getId()));

        if (orders == null || orders.isEmpty()) {
            return ListDto.emptyList();
        }

        List<OrderSimpleDto> dtos = new ArrayList<>(orders.size());
        for (Order order : orders) {
            dtos.add(new OrderSimpleDto(order));
        }

        return new ListDto<>(dtos);
    }

    @Override
    public ListDto<SalesResultDailyDto> getSalesResultDailyThisMonth(UserLogin userLogin) {
        List<SimpleDate> workingDaysUntilToday = calendarConfigRepository.getWorkingDays(userLogin.getClientId(),
                DateTimeUtils.getPeriodThisMonthUntilToday());

        if (workingDaysUntilToday == null || workingDaysUntilToday.isEmpty()) {
            return ListDto.emptyList();
        }

        Set<ObjectId> salesmanIds = getSalesmanIds(userLogin);

        List<SalesResultDailyDto> dtos = new ArrayList<>(workingDaysUntilToday.size());
        for (SimpleDate date : workingDaysUntilToday) {
            double revenue = cacheService.getRevenueByCreated(userLogin.getClientId(), DateType.DAILY, date,
                    getDefaultDistributor(userLogin).getId(), salesmanIds);

            double productivity = cacheService.getProductivityByCreated(userLogin.getClientId(), DateType.DAILY, date,
                    getDefaultDistributor(userLogin).getId(), salesmanIds);

            double nbOrder = cacheService.getNbOrderByCreated(userLogin.getClientId(), DateType.DAILY, date,
                    getDefaultDistributor(userLogin).getId(), salesmanIds);

            dtos.add(new SalesResultDailyDto(date.getIsoDate(),
                    new SalesResultDto(new BigDecimal(revenue), new BigDecimal(productivity), (int) nbOrder)));
        }

        return new ListDto<>(dtos);
    }

    @Override
    public ListDto<OrderSimpleDto> getOrderByDateThisMonth(UserLogin userLogin, String _date) {
        SimpleDate date = getMandatoryIsoDate(_date);

        Set<ObjectId> salesmanIds = getSalesmanIds(userLogin);

        List<Order> orders = orderRepository.getOrdersByCreatedUsers(userLogin.getClientId(), salesmanIds,
                DateTimeUtils.getPeriodOneDay(date), getConfig(userLogin).getOrderDateType(), null);

        if (orders == null || orders.isEmpty()) {
            return ListDto.emptyList();
        }

        List<OrderSimpleDto> dtos = new ArrayList<>(orders.size());
        for (Order order : orders) {
            dtos.add(new OrderSimpleDto(order));
        }

        return new ListDto<>(dtos);
    }

}
