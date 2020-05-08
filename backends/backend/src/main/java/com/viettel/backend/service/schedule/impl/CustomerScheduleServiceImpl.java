package com.viettel.backend.service.schedule.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.viettel.backend.domain.Config;
import com.viettel.backend.domain.Customer;
import com.viettel.backend.domain.Distributor;
import com.viettel.backend.domain.Route;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.domain.embed.Schedule;
import com.viettel.backend.domain.embed.ScheduleItem;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.schedule.CustomerScheduleCreateDto;
import com.viettel.backend.dto.schedule.CustomerScheduleDto;
import com.viettel.backend.dto.schedule.ScheduleItemDto;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.exeption.BusinessExceptionCode;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.CustomerRepository;
import com.viettel.backend.repository.DistributorRepository;
import com.viettel.backend.repository.RouteRepository;
import com.viettel.backend.repository.ScheduleRepository;
import com.viettel.backend.repository.VisitRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.common.AbstractService;
import com.viettel.backend.service.schedule.CustomerScheduleService;

@RolePermission(value = { Role.ADMIN, Role.SUPERVISOR, Role.DISTRIBUTOR })
@Service
public class CustomerScheduleServiceImpl extends AbstractService implements CustomerScheduleService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DistributorRepository distributorRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private VisitRepository visitRepository;

    @Override
    public ListDto<CustomerScheduleDto> getCustomerSchedules(UserLogin userLogin, String _distributorId,
            boolean searchByRoute, String _routeId, String search, Integer dayOfWeek, Pageable pageable) {
        Distributor distributor = getDefaultDistributor(userLogin);
        if (distributor == null) {
            BusinessAssert.notNull(_distributorId);
            distributor = getMandatoryPO(userLogin, _distributorId, distributorRepository);
            BusinessAssert.isTrue(checkAccessible(userLogin, distributor.getId()), "distributor is not accessible");
        }

        ObjectId routeId = null;
        if (searchByRoute && _routeId != null) {
            Route route = getMandatoryPO(userLogin, _routeId, routeRepository);
            BusinessAssert.equals(route.getDistributor().getId(), distributor.getId(), "route not of this distributor");
            routeId = route.getId();
        }

        if (dayOfWeek != null) {
            BusinessAssert.isTrue(dayOfWeek >= Calendar.SUNDAY && dayOfWeek <= Calendar.SATURDAY,
                    "day of week invalid");
        }

        List<Customer> customers = customerRepository.getCustomers(userLogin.getClientId(), distributor.getId(),
                searchByRoute, routeId, dayOfWeek, search, pageable, null);
        if (CollectionUtils.isEmpty(customers) && pageable.getPageNumber() == 0) {
            return ListDto.emptyList();
        }

        List<CustomerScheduleDto> dtos = new ArrayList<CustomerScheduleDto>(customers.size());
        for (Customer customer : customers) {
            dtos.add(new CustomerScheduleDto(customer));
        }

        long size = Long.valueOf(dtos.size());
        if (pageable != null) {
            if (pageable.getPageNumber() > 0 || size == pageable.getPageSize()) {
                size = customerRepository.countCustomers(userLogin.getClientId(), distributor.getId(), searchByRoute,
                        routeId, dayOfWeek, search);
            }
        }

        return new ListDto<CustomerScheduleDto>(dtos, size);
    }

    @Override
    public CustomerScheduleDto getCustomerSchedule(UserLogin userLogin, String customerId) {
        Customer customer = getMandatoryPO(userLogin, customerId, customerRepository);
        BusinessAssert.isTrue(checkAccessible(userLogin, null, customer), "customer no accessible");
        return new CustomerScheduleDto(customer);
    }

    @Override
    public void saveCustomerScheduleByDistributor(UserLogin userLogin, String _distributorId,
            ListDto<CustomerScheduleCreateDto> list) {
        BusinessAssert.notNull(list);

        Distributor distributor = getDefaultDistributor(userLogin);
        if (distributor == null) {
            BusinessAssert.notNull(_distributorId);
            distributor = getMandatoryPO(userLogin, _distributorId, distributorRepository);
            BusinessAssert.isTrue(checkAccessible(userLogin, distributor.getId()), "distributor is not accessible");
        }

        Config config = getConfig(userLogin);

        if (list.getList() != null && !list.getList().isEmpty()) {
            // CHECK IF CUSTOMER IS VISITING
            Set<ObjectId> customerIds = new HashSet<>();
            for (CustomerScheduleCreateDto dto : list.getList()) {
                customerIds.add(getObjectId(dto.getId()));
            }
            BusinessAssert.notTrue(visitRepository.checkVisiting(userLogin.getClientId(), customerIds),
                    BusinessExceptionCode.CUSTOMER_IS_VISITING, "customer is visiting");

            HashMap<ObjectId, Schedule> scheduleByCustomerId = new HashMap<ObjectId, Schedule>();
            for (CustomerScheduleCreateDto dto : list.getList()) {
                BusinessAssert.notNull(dto);

                Schedule schedule = null;
                if (dto.getSchedule() != null && dto.getSchedule().getRouteId() != null) {
                    Route route = getMandatoryPO(userLogin, dto.getSchedule().getRouteId(), routeRepository);

                    if (dto.getSchedule().getItems() != null && !dto.getSchedule().getItems().isEmpty()) {
                        boolean valid = true;
                        List<ScheduleItem> items = new ArrayList<ScheduleItem>(dto.getSchedule().getItems().size());

                        for (ScheduleItemDto itemDto : dto.getSchedule().getItems()) {
                            ScheduleItem item = getScheduleItem(itemDto, config);
                            if (item == null) {
                                valid = false;
                                break;
                            }

                            items.add(item);
                        }

                        if (valid) {
                            schedule = new Schedule();
                            schedule.setRouteId(route.getId());
                            schedule.setItems(items);
                        }
                    }
                }

                scheduleByCustomerId.put(getObjectId(dto.getId()), schedule);
            }

            List<Customer> customers = customerRepository.getListByIds(userLogin.getClientId(),
                    scheduleByCustomerId.keySet());
            BusinessAssert.equals(customers.size(), scheduleByCustomerId.size());

            for (Customer customer : customers) {
                customer.setSchedule(scheduleByCustomerId.get(customer.getId()));
                customerRepository.save(userLogin.getClientId(), customer);
            }

            scheduleRepository.notifyScheduleChange(userLogin.getClientId(), distributor == null ? null : distributor.getId());
        }
    }

    @Override
    public void saveCustomerSchedule(UserLogin userLogin, CustomerScheduleCreateDto dto) {
        BusinessAssert.notNull(dto);

        Customer customer = getMandatoryPO(userLogin, dto.getId(), customerRepository);
        BusinessAssert.isTrue(checkAccessible(userLogin, null, customer), "customer no accessible");

        BusinessAssert.notTrue(
                visitRepository.checkVisiting(userLogin.getClientId(), Collections.singleton(customer.getId())),
                BusinessExceptionCode.CUSTOMER_IS_VISITING, "customer is visiting");

        Config config = getConfig(userLogin);

        Schedule schedule = null;
        if (dto.getSchedule() != null && dto.getSchedule().getRouteId() != null) {
            Route route = getMandatoryPO(userLogin, dto.getSchedule().getRouteId(), routeRepository);

            if (dto.getSchedule().getItems() != null && !dto.getSchedule().getItems().isEmpty()) {
                boolean valid = true;
                List<ScheduleItem> items = new ArrayList<ScheduleItem>(dto.getSchedule().getItems().size());

                for (ScheduleItemDto itemDto : dto.getSchedule().getItems()) {
                    ScheduleItem item = getScheduleItem(itemDto, config);
                    if (item == null) {
                        valid = false;
                        break;
                    }

                    items.add(item);
                }

                if (valid) {
                    schedule = new Schedule();
                    schedule.setRouteId(route.getId());
                    schedule.setItems(items);
                }
            }
        }

        customer.setSchedule(schedule);
        customerRepository.save(userLogin.getClientId(), customer);
        scheduleRepository.notifyScheduleChange(userLogin.getClientId(), customer.getDistributor().getId());
    }

    // PRIVATE
    private ScheduleItem getScheduleItem(ScheduleItemDto dto, Config config) {
        if (dto != null) {
            if (!(dto.isMonday() || dto.isTuesday() || dto.isWednesday() || dto.isThursday() || dto.isFriday()
                    || dto.isSaturday() || dto.isSunday())) {
                return null;
            }

            if (config.getNumberWeekOfFrequency() > 1) {
                if (dto.getWeeks() == null || dto.getWeeks().isEmpty()) {
                    return null;
                }
            }

            ScheduleItem item = new ScheduleItem();
            item.setMonday(dto.isMonday());
            item.setTuesday(dto.isTuesday());
            item.setWednesday(dto.isWednesday());
            item.setThursday(dto.isThursday());
            item.setFriday(dto.isFriday());
            item.setSaturday(dto.isSaturday());
            item.setSunday(dto.isSunday());
            item.setWeeks(dto.getWeeks());
            return item;
        }

        return null;
    }

}
