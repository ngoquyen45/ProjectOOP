package com.viettel.backend.restful.salesman;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.common.IdDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.order.OrderCreateDto;
import com.viettel.backend.dto.order.OrderDto;
import com.viettel.backend.dto.order.OrderPromotionDto;
import com.viettel.backend.dto.order.OrderSimpleDto;
import com.viettel.backend.restful.AbstractController;
import com.viettel.backend.restful.Envelope;
import com.viettel.backend.service.order.OrderCreatingService;
import com.viettel.backend.service.order.OrderSalesmanService;

@RestController(value = "salesmanOrderController")
@RequestMapping(value = "/salesman/order")
public class OrderController extends AbstractController {

    @Autowired
    private OrderCreatingService orderCreatingService;

    @Autowired
    private OrderSalesmanService orderSalesmanService;

    @RequestMapping(value = "/unplanned", method = RequestMethod.POST)
    public ResponseEntity<?> createUnplannedOrder(@RequestBody OrderCreateDto dto) {
        IdDto orderId = orderCreatingService.createOrder(getUserLogin(), dto);
        return new Envelope(orderId).toResponseEntity();
    }

    @RequestMapping(value = "/calculate", method = RequestMethod.POST)
    public ResponseEntity<?> calculatePromotion(@RequestBody OrderCreateDto dto) {
        List<OrderPromotionDto> results = orderCreatingService.calculatePromotion(getUserLogin(), dto);
        return new Envelope(results).toResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/today", method = RequestMethod.GET)
    public ResponseEntity<?> getOrdersCreatedToday(@RequestParam(required = false) String customerId) {
        ListDto<OrderSimpleDto> results = orderCreatingService.getOrdersCreatedToday(getUserLogin(), customerId);
        return new Envelope(results).toResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getOrders(@RequestParam(value = "q", required = false) String search,
            @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        ListDto<OrderSimpleDto> results = orderSalesmanService.getOrders(getUserLogin(), search,
                getPageRequest(page, size));
        return new Envelope(results).toResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> viewOrderDetail(@PathVariable String id) {
        OrderDto result = orderSalesmanService.getOrderById(getUserLogin(), id);
        return new Envelope(result).toResponseEntity(HttpStatus.OK);
    }

}
