package com.viettel.backend.service.order;

import org.springframework.data.domain.Pageable;

import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.order.OrderDto;
import com.viettel.backend.dto.order.OrderSimpleDto;
import com.viettel.backend.oauth2.core.UserLogin;

public interface OrderSalesmanService {

    /**
     * Get all orders approved created by me and all orders approved of my current customers
     */
    public ListDto<OrderSimpleDto> getOrders(UserLogin userLogin, String search, Pageable pageable);
    
    public OrderDto getOrderById(UserLogin userLogin, String id);
    
}
