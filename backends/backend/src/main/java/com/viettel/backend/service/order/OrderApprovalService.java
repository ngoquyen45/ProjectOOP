package com.viettel.backend.service.order;

import org.springframework.data.domain.Pageable;

import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.order.OrderDto;
import com.viettel.backend.dto.order.OrderSimpleDto;
import com.viettel.backend.oauth2.core.UserLogin;

public interface OrderApprovalService {
    
    public ListDto<OrderSimpleDto> getPendingOrders(UserLogin userLogin, Pageable pageable);

    public OrderDto getOrderById(UserLogin userLogin, String orderId);

    public void approve(UserLogin userLogin, String orderId);

    public void reject(UserLogin userLogin, String orderId);

}
