package com.viettel.backend.restful.salesman;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.category.ProductDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.order.ProductForOrderDto;
import com.viettel.backend.restful.AbstractController;
import com.viettel.backend.restful.Envelope;
import com.viettel.backend.service.category.readonly.ProductService;
import com.viettel.backend.service.order.OrderCreatingService;

@RestController("salesmanProductController")
@RequestMapping(value = "salesman/product")
public class ProductController extends AbstractController {

    @Autowired
    private ProductService productService;
    
    @Autowired
    private OrderCreatingService orderCreatingService;
    
    // LIST PRODUCT
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> list(@RequestParam(value = "q", required = false, defaultValue = "") String search,
            @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        ListDto<ProductDto> results = productService.getList(getUserLogin(), search, null, getPageRequest(page, size));
        return new Envelope(results).toResponseEntity(HttpStatus.OK);
    }

    // PRODUCT FOR ORDER
    @RequestMapping(value = "/for-order", method = RequestMethod.GET)
    public ResponseEntity<?> getProductsForOrder(@RequestParam(value = "customerId", required = true) String customerId) {
        ListDto<ProductForOrderDto> results = orderCreatingService.getProductsForOrder(getUserLogin(), customerId);
        return new Envelope(results).toResponseEntity(HttpStatus.OK);
    }

}
