package com.viettel.backend.service.order.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.viettel.backend.domain.Config;
import com.viettel.backend.domain.Distributor;
import com.viettel.backend.domain.Order;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.order.OrderDto;
import com.viettel.backend.dto.order.OrderSimpleDto;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.DistributorRepository;
import com.viettel.backend.repository.OrderRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.common.AbstractService;
import com.viettel.backend.service.order.OrderMonitoringService;
import com.viettel.backend.util.DateTimeUtils;
import com.viettel.backend.util.entity.SimpleDate;
import com.viettel.backend.util.entity.SimpleDate.Period;

@RolePermission(value = { Role.ADMIN, Role.OBSERVER, Role.SUPERVISOR, Role.DISTRIBUTOR })
@Service
public class OrderMonitoringServiceImpl extends AbstractService implements OrderMonitoringService {

    @Autowired
    private DistributorRepository distributorRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public ListDto<OrderSimpleDto> getOrders(UserLogin userLogin, String _distributorId, String _fromDate,
            String _toDate, Pageable pageable) {
        SimpleDate fromDate = getMandatoryIsoDate(_fromDate);
        SimpleDate toDate = getMandatoryIsoDate(_toDate);
        BusinessAssert.isTrue(fromDate.compareTo(toDate) <= 0, "fromDate > toDate");
        BusinessAssert.isTrue(DateTimeUtils.addMonths(fromDate, 2).compareTo(toDate) >= 0, "greater than 2 month");

        Period period = new Period(fromDate, DateTimeUtils.addDays(toDate, 1));

        Set<ObjectId> distributorIds = null;
        if (_distributorId == null) {
            List<Distributor> distributors = getAccessibleDistributors(userLogin);
            distributorIds = getIdSet(distributors);
        } else {
            Distributor distributor = getMandatoryPO(userLogin, _distributorId, distributorRepository);
            BusinessAssert.isTrue(checkAccessible(userLogin, distributor.getId()), "distributor is not accessible");

            distributorIds = Collections.singleton(distributor.getId());
        }

        Sort sort = new Sort(Direction.DESC, Order.COLUMNNAME_APPROVE_TIME_VALUE);

        Config config = getConfig(userLogin);

        Collection<Order> orders = orderRepository.getOrdersByDistributors(userLogin.getClientId(), distributorIds,
                period, config.getOrderDateType(), pageable, sort);
        if (CollectionUtils.isEmpty(orders) && pageable.getPageNumber() == 0) {
            ListDto.emptyList();
        }

        List<OrderSimpleDto> dtos = new ArrayList<OrderSimpleDto>(orders.size());
        for (Order order : orders) {
            dtos.add(new OrderSimpleDto(order));
        }

        long size = Long.valueOf(dtos.size());
        if (pageable != null) {
            if (pageable.getPageNumber() > 0 || pageable.getPageSize() == size) {
                size = orderRepository.countOrdersByDistributors(userLogin.getClientId(), distributorIds, period,
                        config.getOrderDateType());
            }
        }

        return new ListDto<OrderSimpleDto>(dtos, size);
    }

    @Override
    public OrderDto getOrderById(UserLogin userLogin, String _orderId) {
        Order order = getMandatoryPO(userLogin, _orderId, orderRepository);

        BusinessAssert.isTrue(checkAccessible(userLogin, order.getDistributor().getId()),
                "distributor is not accessible");

        return new OrderDto(order, getProductPhotoFactory(userLogin));
    }

}
