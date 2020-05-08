package com.viettel.backend.service.order;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.viettel.backend.dto.category.CustomerSimpleDto;
import com.viettel.backend.dto.common.IdDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.order.OrderCreateDto;
import com.viettel.backend.dto.order.OrderDto;
import com.viettel.backend.dto.order.OrderPromotionDto;
import com.viettel.backend.dto.order.OrderSimpleDto;
import com.viettel.backend.dto.order.ProductForOrderDto;
import com.viettel.backend.oauth2.core.UserLogin;

public interface OrderCreatingService {
    
    public ListDto<CustomerSimpleDto> getCustomersForOrder(UserLogin userLogin, String search, Pageable pageable);

    public ListDto<ProductForOrderDto> getProductsForOrder(UserLogin userLogin, String customerId);
    
    public List<OrderPromotionDto> calculatePromotion(UserLogin userLogin, OrderCreateDto orderCreateDto);
    
    public IdDto createOrder(UserLogin userLogin, OrderCreateDto orderCreateDto);
    
    // ALL Orders (draft + approved + rejected)
    public ListDto<OrderSimpleDto> getOrdersCreatedToday(UserLogin userLogin, String customerId);

    public OrderDto getOrderById(UserLogin userLogin, String id);
    
}
