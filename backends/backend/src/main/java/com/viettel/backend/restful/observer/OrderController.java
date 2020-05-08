package com.viettel.backend.restful.observer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.order.OrderDto;
import com.viettel.backend.dto.order.OrderSimpleDto;
import com.viettel.backend.restful.AbstractController;
import com.viettel.backend.restful.Envelope;
import com.viettel.backend.service.order.OrderMonitoringService;

@RestController(value = "observerOrderController")
@RequestMapping(value = "/observer/order")
public class OrderController extends AbstractController {

    @Autowired
    private OrderMonitoringService orderMonitoringService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public final ResponseEntity<?> getOrders(@RequestParam(required = false) String distributorId,
            @RequestParam(required = true) String fromDate, @RequestParam(required = true) String toDate,
            @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        ListDto<OrderSimpleDto> list = orderMonitoringService.getOrders(getUserLogin(), distributorId, fromDate, toDate,
                getPageRequest(page, size));
        return new Envelope(list).toResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public final ResponseEntity<?> getOrderById(@PathVariable String id) {
        OrderDto dto = orderMonitoringService.getOrderById(getUserLogin(), id);
        return new Envelope(dto).toResponseEntity(HttpStatus.OK);
    }

}
