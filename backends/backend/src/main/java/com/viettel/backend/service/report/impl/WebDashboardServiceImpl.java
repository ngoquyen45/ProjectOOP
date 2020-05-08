package com.viettel.backend.service.report.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viettel.backend.domain.Distributor;
import com.viettel.backend.domain.Product;
import com.viettel.backend.domain.Route;
import com.viettel.backend.domain.Target;
import com.viettel.backend.domain.User;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.dto.common.ProgressDto;
import com.viettel.backend.dto.report.WebDashboardDto;
import com.viettel.backend.dto.report.WebDashboardDto.BestSellerItem;
import com.viettel.backend.dto.report.WebDashboardDto.ProgressWarningItem;
import com.viettel.backend.dto.report.WebDashboardDto.Result;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.CalendarConfigRepository;
import com.viettel.backend.repository.ProductRepository;
import com.viettel.backend.repository.RouteRepository;
import com.viettel.backend.repository.ScheduleRepository;
import com.viettel.backend.repository.TargetRepository;
import com.viettel.backend.repository.UserRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.common.AbstractService;
import com.viettel.backend.service.common.CacheService;
import com.viettel.backend.service.common.CacheService.DateType;
import com.viettel.backend.service.report.WebDashboardService;
import com.viettel.backend.util.DateTimeUtils;
import com.viettel.backend.util.entity.SimpleDate;
import com.viettel.backend.util.entity.SimpleDate.Period;

@RolePermission(value = { Role.ADMIN, Role.OBSERVER, Role.SUPERVISOR, Role.DISTRIBUTOR })
@Service
public class WebDashboardServiceImpl extends AbstractService implements WebDashboardService {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private CalendarConfigRepository calendarConfigRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private TargetRepository targetRepository;

    @Override
    public WebDashboardDto getWebDashboard(UserLogin userLogin) {
        SimpleDate today = DateTimeUtils.getToday();

        WebDashboardDto dto = new WebDashboardDto(today);

        List<Distributor> distributors = getAccessibleDistributors(userLogin);
        Set<ObjectId> distributorIds = getIdSet(distributors);

        List<User> salesmen = userRepository.getSalesmenByDistributors(userLogin.getClientId(), distributorIds);
        Set<ObjectId> salesmanIds = getIdSet(salesmen);

        // CALCULATE NB VISIT PLANNED
        List<Route> routes = routeRepository.getAll(userLogin.getClientId(), distributorIds);

        Map<ObjectId, Integer> nbVisitPlannedByRouteToday = scheduleRepository
                .getNbVisitPlannedByRoute(userLogin.getClientId(), distributorIds, DateTimeUtils.getPeriodToday());
        int nbVisitPlannedToday = 0;

        Map<ObjectId, Integer> nbVisitPlannedByRouteThisMonth = scheduleRepository.getNbVisitPlannedByRoute(
                userLogin.getClientId(), distributorIds, new Period(DateTimeUtils.getFirstOfThisMonth(), today));
        Map<ObjectId, Integer> nbVisitPlannedBySalesmanThisMonth = new HashMap<ObjectId, Integer>();

        for (Route route : routes) {
            Integer tmpToday = nbVisitPlannedByRouteToday.get(route.getId());
            tmpToday = tmpToday == null ? 0 : tmpToday;

            nbVisitPlannedToday += tmpToday;

            Integer tmpThisMonth = nbVisitPlannedByRouteThisMonth.get(route.getId());
            tmpThisMonth = tmpThisMonth == null ? 0 : tmpThisMonth;

            if (route.getSalesman() != null) {
                Integer value = nbVisitPlannedBySalesmanThisMonth.get(route.getSalesman().getId());
                value = value == null ? 0 : value;

                nbVisitPlannedBySalesmanThisMonth.put(route.getSalesman().getId(), value + tmpThisMonth);
            }
        }

        // TODAY
        Result todayResult = new Result();
        todayResult.setRevenue(new ProgressDto(
                cacheService.getRevenueByDistributor(userLogin.getClientId(), DateType.DAILY, today, distributorIds)));
        todayResult.setOrder(new ProgressDto(
                cacheService.getNbOrderByDistributor(userLogin.getClientId(), DateType.DAILY, today, distributorIds)));
        todayResult.setOrderNoVisit(new ProgressDto(cacheService.getNbOrderNoVisitByDistributor(userLogin.getClientId(),
                DateType.DAILY, today, distributorIds)));

        todayResult.setVisit(new ProgressDto((double) nbVisitPlannedToday,
                cacheService.getNbVisitByDistributor(userLogin.getClientId(), DateType.DAILY, today, distributorIds)));
        todayResult.setVisitHasOrder(new ProgressDto(cacheService
                .getNbVisitWithOrderByDistributor(userLogin.getClientId(), DateType.DAILY, today, distributorIds)));
        todayResult.setVisitErrorDuration(new ProgressDto(cacheService
                .getNbVisitErrorDurationByDistributor(userLogin.getClientId(), DateType.DAILY, today, distributorIds)));
        todayResult.setVisitErrorPosition(new ProgressDto(cacheService
                .getNbVisitErrorPositionByDistributor(userLogin.getClientId(), DateType.DAILY, today, distributorIds)));

        dto.setTodayResult(todayResult);

        // THIS MONTH
        // PLAN
        List<Target> targets = targetRepository.getTargetsBySalesmen(userLogin.getClientId(), salesmanIds,
                today.getMonth(), today.getYear(), null);
        BigDecimal revenueTargetThisMonth = BigDecimal.ZERO;
        if (targets != null) {
            for (Target target : targets) {
                revenueTargetThisMonth = revenueTargetThisMonth.add(target.getRevenue());
            }
        }
        // ACTUAL
        Result thisMonthResult = new Result();
        thisMonthResult.setNbDay(calendarConfigRepository
                .getWorkingDays(userLogin.getClientId(), new Period(DateTimeUtils.getFirstOfThisMonth(), today))
                .size());
        thisMonthResult.setRevenue(new ProgressDto(revenueTargetThisMonth.doubleValue(),
                cacheService.getRevenueByDistributor(userLogin.getClientId(), DateType.MONTHLY, today, distributorIds)
                        - todayResult.getRevenue().getActual().doubleValue()));
        thisMonthResult.setOrder(new ProgressDto(
                cacheService.getNbOrderByDistributor(userLogin.getClientId(), DateType.MONTHLY, today, distributorIds)
                        - todayResult.getOrder().getActual().doubleValue()));
        thisMonthResult.setVisit(new ProgressDto(
                cacheService.getNbVisitByDistributor(userLogin.getClientId(), DateType.MONTHLY, today, distributorIds)
                        - todayResult.getVisit().getActual().doubleValue()));

        // SALESMAN THIS MONTH
        HashMap<ObjectId, Double> nbVisitedBySalesmanThisMonth = new HashMap<>();
        HashMap<ObjectId, Double> nbVisitedBySalesmanToday = new HashMap<>();
        for (ObjectId distributorId : distributorIds) {
            nbVisitedBySalesmanThisMonth.putAll(cacheService.getNbVisitBySalesmanMap(userLogin.getClientId(),
                    DateType.MONTHLY, today, distributorId));
            nbVisitedBySalesmanToday.putAll(cacheService.getNbVisitBySalesmanMap(userLogin.getClientId(),
                    DateType.DAILY, today, distributorId));
        }

        List<ProgressWarningItem> salesmanProgress = new ArrayList<>(salesmen.size());
        for (User salesman : salesmen) {
            Double actualThisMonth = nbVisitedBySalesmanThisMonth.get(salesman.getId());
            Double actualToday = nbVisitedBySalesmanToday.get(salesman.getId());
            int actual = (int) ((actualThisMonth == null ? 0 : actualThisMonth)
                    - (actualToday == null ? 0 : actualToday));

            ProgressWarningItem item = new ProgressWarningItem(salesman.getFullname(),
                    new ProgressDto(nbVisitPlannedBySalesmanThisMonth.get(salesman.getId()), actual));
            salesmanProgress.add(item);
        }

        Collections.sort(salesmanProgress, new Comparator<ProgressWarningItem>() {

            @Override
            public int compare(ProgressWarningItem o1, ProgressWarningItem o2) {
                return o1.getProgress().getPercentage().compareTo(o2.getProgress().getPercentage());
            }

        });
        dto.setProgressWarnings(salesmanProgress.subList(0, salesmanProgress.size() > 5 ? 5 : salesmanProgress.size()));

        // BEST SELLERS
        HashMap<ObjectId, Double> quantityByProductThisMonth = new HashMap<>();
        HashMap<ObjectId, Double> quantityByProductToday = new HashMap<>();
        for (ObjectId distributorId : distributorIds) {
            quantityByProductThisMonth.putAll(cacheService.getQuantityProductMap(userLogin.getClientId(),
                    DateType.MONTHLY, today, distributorId));
            quantityByProductToday.putAll(
                    cacheService.getQuantityProductMap(userLogin.getClientId(), DateType.DAILY, today, distributorId));
        }

        List<Product> products = productRepository.getAll(userLogin.getClientId(), null);
        List<BestSellerItem> bestSellers = new ArrayList<>(products.size());
        for (Product product : products) {
            Double quantityThisMonth = quantityByProductThisMonth.get(product.getId());
            Double quantityToday = quantityByProductToday.get(product.getId());
            double quantity = (quantityThisMonth == null ? 0 : quantityThisMonth)
                    - (quantityToday == null ? 0 : quantityToday);

            BestSellerItem item = new BestSellerItem(product.getName(), new BigDecimal(quantity));
            bestSellers.add(item);
        }
        Collections.sort(bestSellers, new Comparator<BestSellerItem>() {

            @Override
            public int compare(BestSellerItem o1, BestSellerItem o2) {
                return o2.getResult().compareTo(o1.getResult());
            }

        });
        dto.setBestSellers(bestSellers.subList(0, bestSellers.size() > 5 ? 5 : bestSellers.size()));

        dto.setThisMonthResult(thisMonthResult);

        // LAST MONTH
        SimpleDate firstOfLastMonth = DateTimeUtils.getFirstOfLastMonth();

        Result lastMonthResult = new Result();
        lastMonthResult.setNbDay(calendarConfigRepository
                .getWorkingDays(userLogin.getClientId(), DateTimeUtils.getPeriodLastMonth()).size());
        lastMonthResult.setRevenue(new ProgressDto(cacheService.getRevenueByDistributor(userLogin.getClientId(),
                DateType.MONTHLY, firstOfLastMonth, distributorIds)));
        lastMonthResult.setOrder(new ProgressDto(cacheService.getNbOrderByDistributor(userLogin.getClientId(),
                DateType.MONTHLY, firstOfLastMonth, distributorIds)));
        lastMonthResult.setVisit(new ProgressDto(cacheService.getNbVisitByDistributor(userLogin.getClientId(),
                DateType.MONTHLY, firstOfLastMonth, distributorIds)));

        dto.setLastMonthResult(lastMonthResult);

        return dto;
    }

}
