package com.viettel.backend.service.order.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.viettel.backend.domain.Customer;
import com.viettel.backend.domain.Order;
import com.viettel.backend.domain.Route;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.order.OrderDto;
import com.viettel.backend.dto.order.OrderSimpleDto;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.CustomerRepository;
import com.viettel.backend.repository.OrderRepository;
import com.viettel.backend.repository.RouteRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.common.AbstractService;
import com.viettel.backend.service.order.OrderSalesmanService;

@RolePermission(value = { Role.SALESMAN })
@Service
public class OrderSalesmanServiceImpl extends AbstractService implements OrderSalesmanService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public ListDto<OrderSimpleDto> getOrders(UserLogin userLogin, String search, Pageable pageable) {
        Collection<ObjectId> salesmanIds = Collections.singleton(userLogin.getUserId());

        List<Route> routes = routeRepository.getRoutesBySalesmen(userLogin.getClientId(), salesmanIds);
        List<Customer> customers = customerRepository.getCustomersByRoutes(userLogin.getClientId(), getIdSet(routes),
                null);
        Collection<ObjectId> customerIds = getIdSet(customers);

        List<Order> orders = orderRepository.getOrdersByCreatedUsersOrCustomers(userLogin.getClientId(), salesmanIds,
                customerIds, search, pageable);
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
                size = orderRepository.countOrdersByCreatedUsersOrCustomers(userLogin.getClientId(), salesmanIds,
                customerIds, search);
            }
        }

        return new ListDto<OrderSimpleDto>(dtos, size);
    }

    @Override
    public OrderDto getOrderById(UserLogin userLogin, String _orderId) {
        Order order = getMandatoryPO(userLogin, _orderId, orderRepository);

        BusinessAssert.equals(userLogin.getUserId(), order.getCreatedBy().getId(), "order is not created by me");
        
        Collection<ObjectId> salesmanIds = Collections.singleton(userLogin.getUserId());
        List<Route> routes = routeRepository.getRoutesBySalesmen(userLogin.getClientId(), salesmanIds);
        List<Customer> customers = customerRepository.getCustomersByRoutes(userLogin.getClientId(), getIdSet(routes),
                null);
        Collection<ObjectId> customerIds = getIdSet(customers);

        BusinessAssert.isTrue(customerIds.contains(order.getCustomer().getId()));

        return new OrderDto(order, getProductPhotoFactory(userLogin));
    }

}
