package com.viettel.backend.service.visit.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.viettel.backend.domain.Customer;
import com.viettel.backend.domain.Distributor;
import com.viettel.backend.domain.Route;
import com.viettel.backend.domain.User;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.domain.Visit;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.common.ProgressDto;
import com.viettel.backend.dto.visit.CustomerForVisitDto;
import com.viettel.backend.dto.visit.VisitInfoDto;
import com.viettel.backend.dto.visit.VisitInfoListDto;
import com.viettel.backend.dto.visit.VisitTodaySummary;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.CustomerRepository;
import com.viettel.backend.repository.DistributorRepository;
import com.viettel.backend.repository.RouteRepository;
import com.viettel.backend.repository.ScheduleRepository;
import com.viettel.backend.repository.UserRepository;
import com.viettel.backend.repository.VisitRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.common.AbstractService;
import com.viettel.backend.service.common.CacheService;
import com.viettel.backend.service.common.CacheService.DateType;
import com.viettel.backend.service.visit.VisitMonitoringService;
import com.viettel.backend.util.DateTimeUtils;
import com.viettel.backend.util.entity.SimpleDate;
import com.viettel.backend.util.entity.SimpleDate.Period;

@RolePermission(value = { Role.ADMIN, Role.OBSERVER, Role.SUPERVISOR, Role.DISTRIBUTOR })
@Service
public class VisitMonitoringServiceImpl extends AbstractService implements VisitMonitoringService {

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DistributorRepository distributorRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private CacheService cacheService;

    @Override
    public ListDto<VisitInfoListDto> getVisitsToday(UserLogin userLogin, String _distributorId, String _salesmanId,
            Pageable pageable) {
        Distributor distributor = getDefaultDistributor(userLogin);
        if (distributor == null) {
            BusinessAssert.notNull(_distributorId);
            distributor = getMandatoryPO(userLogin, _distributorId, distributorRepository);
            checkAccessible(userLogin, distributor.getId());
        }

        Sort sort = new Sort(Direction.DESC, Visit.COLUMNNAME_START_TIME_VALUE);
        Period todayPeriod = DateTimeUtils.getPeriodToday();

        List<Visit> visits = null;
        long size = 0;
        if (_salesmanId != null) {
            User salesman = getMandatoryPO(userLogin, _salesmanId, userRepository);
            BusinessAssert.equals(salesman.getRole(), Role.SALESMAN, "not salesman");
            BusinessAssert.notNull(salesman.getDistributor());
            BusinessAssert.equals(salesman.getDistributor().getId(), distributor.getId(),
                    "salesman not of distributor");

            visits = visitRepository.getVisitedsBySalesmen(userLogin.getClientId(),
                    Collections.singleton(salesman.getId()), todayPeriod, pageable, sort);
            if (CollectionUtils.isEmpty(visits) && pageable.getPageNumber() == 0) {
                return ListDto.emptyList();
            }

            size = Long.valueOf(visits.size());
            if (pageable != null) {
                if (pageable.getPageNumber() > 0 || size == pageable.getPageSize()) {
                    size = visitRepository.countVisitedsBySalesmen(userLogin.getClientId(),
                            Collections.singleton(salesman.getId()), todayPeriod);
                }
            }
        } else {
            visits = visitRepository.getVisitedsByDistributors(userLogin.getClientId(),
                    Collections.singleton(distributor.getId()), todayPeriod, pageable, sort);
            if (CollectionUtils.isEmpty(visits) && pageable.getPageNumber() == 0) {
                return ListDto.emptyList();
            }

            size = Long.valueOf(visits.size());
            if (pageable != null) {
                if (pageable.getPageNumber() > 0 || size == pageable.getPageSize()) {
                    size = visitRepository.countVisitedsByDistributors(userLogin.getClientId(),
                            Collections.singleton(distributor.getId()), todayPeriod);
                }
            }
        }

        List<VisitInfoListDto> dtos = new ArrayList<VisitInfoListDto>(visits.size());
        for (Visit visit : visits) {
            dtos.add(new VisitInfoListDto(visit));
        }

        return new ListDto<>(dtos, size);
    }

    @Override
    public ListDto<CustomerForVisitDto> getCustomersTodayBySalesman(UserLogin userLogin, String _salesmanId) {
        User salesman = getMandatoryPO(userLogin, _salesmanId, userRepository);
        BusinessAssert.isTrue(checkAccessible(userLogin, salesman.getDistributor().getId()), "salesman not accessible");

        SimpleDate today = DateTimeUtils.getToday();

        Set<ObjectId> routeIds = getIdSet(
                routeRepository.getRoutesBySalesmen(userLogin.getClientId(), Collections.singleton(salesman.getId())));

        List<Customer> customers = customerRepository.getCustomersByRoutes(userLogin.getClientId(), routeIds, today);

        if (customers == null || customers.isEmpty()) {
            return ListDto.emptyList();
        }

        Set<ObjectId> customerIds = getIdSet(customers);

        // GET VISITED TODAY
        Map<ObjectId, Visit> visitByCustomer = visitRepository.getMapVisitByCustomerIdsToday(userLogin.getClientId(),
                customerIds);

        List<CustomerForVisitDto> dtos = new LinkedList<CustomerForVisitDto>();
        for (Customer customer : customers) {
            Visit visit = visitByCustomer != null ? visitByCustomer.get(customer.getId()) : null;
            dtos.add(new CustomerForVisitDto(customer, true, 0, visit));
        }

        return new ListDto<CustomerForVisitDto>(dtos);
    }

    @Override
    public ListDto<VisitInfoListDto> getVisits(UserLogin userLogin, String _distributorId, String _salesmanId,
            String _fromDate, String _toDate, Pageable pageable) {
        SimpleDate fromDate = getMandatoryIsoDate(_fromDate);
        SimpleDate toDate = getMandatoryIsoDate(_toDate);
        BusinessAssert.isTrue(fromDate.compareTo(toDate) <= 0, "fromDate > toDate");
        BusinessAssert.isTrue(DateTimeUtils.addMonths(fromDate, 2).compareTo(toDate) >= 0, "greater than 2 month");

        Distributor distributor = null;
        Set<ObjectId> distributorIds = null;
        if (_distributorId == null) {
            distributor = getDefaultDistributor(userLogin);
            if (distributor == null) {
                List<Distributor> distributors = getAccessibleDistributors(userLogin);
                distributorIds = getIdSet(distributors);
            } else {
                distributorIds = Collections.singleton(distributor.getId());
            }

        } else {
            distributor = getMandatoryPO(userLogin, _distributorId, distributorRepository);
            BusinessAssert.isTrue(checkAccessible(userLogin, distributor.getId()), "distributor is not accessible");
            distributorIds = Collections.singleton(distributor.getId());
        }

        Period period = new Period(fromDate, DateTimeUtils.addDays(toDate, 1));
        Sort sort = new Sort(Direction.DESC, Visit.COLUMNNAME_START_TIME_VALUE);

        List<Visit> visits = null;
        long size = 0;
        if (distributor != null && _salesmanId != null) {
            User salesman = getMandatoryPO(userLogin, _salesmanId, userRepository);
            BusinessAssert.equals(salesman.getRole(), Role.SALESMAN, "not salesman");
            BusinessAssert.notNull(salesman.getDistributor());
            BusinessAssert.equals(salesman.getDistributor().getId(), distributor.getId(),
                    "salesman not of distributor");

            visits = visitRepository.getVisitedsBySalesmen(userLogin.getClientId(),
                    Collections.singleton(salesman.getId()), period, pageable, sort);
            if (CollectionUtils.isEmpty(visits) && pageable.getPageNumber() == 0) {
                return ListDto.emptyList();
            }

            size = Long.valueOf(visits.size());
            if (pageable != null) {
                if (pageable.getPageNumber() > 0 || size == pageable.getPageSize()) {
                    size = visitRepository.countVisitedsBySalesmen(userLogin.getClientId(),
                            Collections.singleton(salesman.getId()), period);
                }
            }
        } else {
            visits = visitRepository.getVisitedsByDistributors(userLogin.getClientId(), distributorIds, period,
                    pageable, sort);
            if (CollectionUtils.isEmpty(visits) && pageable.getPageNumber() == 0) {
                return ListDto.emptyList();
            }

            size = Long.valueOf(visits.size());
            if (pageable != null) {
                if (pageable.getPageNumber() > 0 || size == pageable.getPageSize()) {
                    size = visitRepository.countVisitedsByDistributors(userLogin.getClientId(), distributorIds, period);
                }
            }
        }

        List<VisitInfoListDto> dtos = new ArrayList<VisitInfoListDto>(visits.size());
        for (Visit visit : visits) {
            dtos.add(new VisitInfoListDto(visit));
        }

        return new ListDto<>(dtos, size);
    }

    @Override
    public VisitInfoDto getVisitInfoById(UserLogin userLogin, String id) {
        Visit visit = getMandatoryPO(userLogin, id, visitRepository);
        checkAccessible(userLogin, visit.getDistributor().getId());
        return new VisitInfoDto(visit, getProductPhotoFactory(userLogin));
    }

    @Override
    public VisitTodaySummary getVisitTodaySummary(UserLogin userLogin, String _distributorId, String _salesmanId) {
        Distributor distributor = getDefaultDistributor(userLogin);
        if (distributor == null) {
            BusinessAssert.notNull(_distributorId);
            distributor = getMandatoryPO(userLogin, _distributorId, distributorRepository);
            checkAccessible(userLogin, distributor.getId());
        }

        SimpleDate today = DateTimeUtils.getToday();
        Collection<ObjectId> distributorIds = Collections.singleton(distributor.getId());
        Map<ObjectId, Integer> nbVisitPlannedByRouteToday = scheduleRepository
                .getNbVisitPlannedByRoute(userLogin.getClientId(), distributorIds, DateTimeUtils.getPeriodToday());

        if (_salesmanId != null) {
            User salesman = getMandatoryPO(userLogin, _salesmanId, userRepository);
            BusinessAssert.equals(salesman.getRole(), Role.SALESMAN, "not salesman");
            BusinessAssert.notNull(salesman.getDistributor());
            BusinessAssert.equals(salesman.getDistributor().getId(), distributor.getId(),
                    "salesman not of distributor");

            Collection<ObjectId> salesmanIds = Collections.singleton(salesman.getId());

            int nbVisitPlannedToday = 0;
            List<Route> routes = routeRepository.getRoutesBySalesmen(userLogin.getClientId(), salesmanIds);
            for (Route route : routes) {
                Integer tmpToday = nbVisitPlannedByRouteToday.get(route.getId());
                tmpToday = tmpToday == null ? 0 : tmpToday;
                nbVisitPlannedToday += tmpToday;
            }

            VisitTodaySummary visitTodaySummary = new VisitTodaySummary();
            visitTodaySummary.setVisit(new ProgressDto((double) nbVisitPlannedToday, cacheService.getNbVisitByCreated(
                    userLogin.getClientId(), DateType.DAILY, today, distributor.getId(), salesmanIds)));
            visitTodaySummary.setVisitErrorDuration(new ProgressDto(cacheService.getNbVisitErrorDurationByCreated(
                    userLogin.getClientId(), DateType.DAILY, today, distributor.getId(), salesmanIds)));
            visitTodaySummary.setVisitErrorPosition(new ProgressDto(cacheService.getNbVisitErrorPositionByCreated(
                    userLogin.getClientId(), DateType.DAILY, today, distributor.getId(), salesmanIds)));

            return visitTodaySummary;
        } else {
            int nbVisitPlannedToday = 0;
            List<Route> routes = routeRepository.getAll(userLogin.getClientId(), distributorIds);
            for (Route route : routes) {
                Integer tmpToday = nbVisitPlannedByRouteToday.get(route.getId());
                tmpToday = tmpToday == null ? 0 : tmpToday;
                nbVisitPlannedToday += tmpToday;
            }

            VisitTodaySummary visitTodaySummary = new VisitTodaySummary();
            visitTodaySummary.setVisit(new ProgressDto((double) nbVisitPlannedToday, cacheService
                    .getNbVisitByDistributor(userLogin.getClientId(), DateType.DAILY, today, distributorIds)));
            visitTodaySummary.setVisitErrorDuration(new ProgressDto(cacheService.getNbVisitErrorDurationByDistributor(
                    userLogin.getClientId(), DateType.DAILY, today, distributorIds)));
            visitTodaySummary.setVisitErrorPosition(new ProgressDto(cacheService.getNbVisitErrorPositionByDistributor(
                    userLogin.getClientId(), DateType.DAILY, today, distributorIds)));

            return visitTodaySummary;
        }
    }

}
