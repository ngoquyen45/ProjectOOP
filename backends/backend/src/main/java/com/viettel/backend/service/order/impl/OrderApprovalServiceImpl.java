package com.viettel.backend.service.order.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
import com.viettel.backend.domain.Order;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.domain.embed.UserEmbed;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.order.OrderDto;
import com.viettel.backend.dto.order.OrderSimpleDto;
import com.viettel.backend.engine.notification.WebNotificationEngine;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.OrderPendingRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.common.AbstractService;
import com.viettel.backend.service.common.CacheService;
import com.viettel.backend.service.order.OrderApprovalService;
import com.viettel.backend.util.DateTimeUtils;

@RolePermission(value = { Role.ADMIN, Role.SUPERVISOR, Role.DISTRIBUTOR })
@Service
public class OrderApprovalServiceImpl extends AbstractService implements OrderApprovalService {

    @Autowired
    private OrderPendingRepository orderPendingRepository;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private WebNotificationEngine webNotificationEngine;

    @Override
    public ListDto<OrderSimpleDto> getPendingOrders(UserLogin userLogin, Pageable pageable) {
        List<Distributor> distributors = getAccessibleDistributors(userLogin);
        if (distributors == null || distributors.isEmpty()) {
            return ListDto.emptyList();
        }

        Sort sort = new Sort(Direction.DESC, Order.COLUMNNAME_CREATED_TIME_VALUE);

        Set<ObjectId> distributorIds = getIdSet(distributors);

        Collection<Order> orders = orderPendingRepository.getPendingOrdersByDistributors(userLogin.getClientId(),
                distributorIds, pageable, sort);
        if (CollectionUtils.isEmpty(orders) && pageable.getPageNumber() == 0) {
            return ListDto.emptyList();
        }

        List<OrderSimpleDto> dtos = new ArrayList<OrderSimpleDto>(orders.size());
        for (Order order : orders) {
            dtos.add(new OrderSimpleDto(order));
        }

        long size = Long.valueOf(dtos.size());
        if (pageable != null) {
            if (pageable.getPageNumber() > 0 || pageable.getPageSize() == size) {
                size = orderPendingRepository.countPendingOrdersByDistributors(userLogin.getClientId(), distributorIds);
            }
        }

        return new ListDto<OrderSimpleDto>(dtos, size);
    }

    @Override
    public OrderDto getOrderById(UserLogin userLogin, String _orderId) {
        Order order = getMandatoryPO(userLogin, _orderId, orderPendingRepository);
        BusinessAssert.isTrue(checkAccessible(userLogin, order.getDistributor().getId()), "distributor not accessible");
        BusinessAssert.equals(order.getApproveStatus(), Customer.APPROVE_STATUS_PENDING, "order not pending");

        return new OrderDto(order, getProductPhotoFactory(userLogin));
    }

    @Override
    public void approve(UserLogin userLogin, String _orderId) {
        Order order = getMandatoryPO(userLogin, _orderId, orderPendingRepository);

        BusinessAssert.isTrue(checkAccessible(userLogin, order.getDistributor().getId()),
                "distributor is not accessible");

        BusinessAssert.equals(order.getApproveStatus(), Order.APPROVE_STATUS_PENDING, "order is not pending");

        order.setApproveStatus(Order.APPROVE_STATUS_APPROVED);
        order.setApproveTime(DateTimeUtils.getCurrentTime());
        order.setApproveUser(new UserEmbed(getCurrentUser(userLogin)));

        order = orderPendingRepository.save(userLogin.getClientId(), order);

        cacheService.addNewApprovedOrder(order);

        webNotificationEngine.notifyChangedOrder(userLogin, order, WebNotificationEngine.ACTION_ORDER_APPROVE);
    }

    @Override
    public void reject(UserLogin userLogin, String _orderId) {
        Order order = getMandatoryPO(userLogin, _orderId, orderPendingRepository);

        BusinessAssert.isTrue(checkAccessible(userLogin, order.getDistributor().getId()),
                "distributor is not accessible");

        BusinessAssert.equals(order.getApproveStatus(), Order.APPROVE_STATUS_PENDING, "order is not pending");

        order.setApproveStatus(Order.APPROVE_STATUS_REJECTED);
        order.setApproveTime(DateTimeUtils.getCurrentTime());
        order.setApproveUser(new UserEmbed(getCurrentUser(userLogin)));

        order = orderPendingRepository.save(userLogin.getClientId(), order);

        webNotificationEngine.notifyChangedOrder(userLogin, order, WebNotificationEngine.ACTION_ORDER_REJECT);
    }

}
