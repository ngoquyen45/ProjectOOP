package com.viettel.backend.restful.salesman;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.common.CategorySimpleDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.restful.AbstractController;
import com.viettel.backend.restful.Envelope;
import com.viettel.backend.service.category.readonly.CustomerTypeService;

@RestController(value="salesmanCustomerTypeController")
@RequestMapping(value = "/salesman/customertype")
public class CustomerTypeController extends AbstractController {

    @Autowired
    private CustomerTypeService customerTypeService;

    // LIST ALL
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<?> listAvailable() {
        ListDto<CategorySimpleDto> dtos = customerTypeService.getAll(getUserLogin(), null);
        return new Envelope(dtos).toResponseEntity(HttpStatus.OK);
    }

}
