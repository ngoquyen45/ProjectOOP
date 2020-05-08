package com.viettel.backend.restful.salesman;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.category.CustomerCreateDto;
import com.viettel.backend.dto.category.CustomerListDto;
import com.viettel.backend.dto.common.IdDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.common.StringDto;
import com.viettel.backend.dto.visit.CustomerForVisitDto;
import com.viettel.backend.dto.visit.CustomerSummaryDto;
import com.viettel.backend.restful.AbstractController;
import com.viettel.backend.restful.Envelope;
import com.viettel.backend.restful.Meta;
import com.viettel.backend.service.customer.CustomerRegisterService;
import com.viettel.backend.service.visit.VisitService;
import com.viettel.backend.util.entity.Location;

@RestController(value = "salesmanCustomerController")
@RequestMapping(value = "/salesman/customer")
public class CustomerController extends AbstractController {

    @Autowired
    private VisitService visitService;
    
    @Autowired
    private CustomerRegisterService customerCreatingService;
    
    // CUSTOMER SUMMARY
    @RequestMapping(value = "/{id}/summary", method = RequestMethod.GET)
    public ResponseEntity<?> getCustomerSummary(@PathVariable String id) {
        CustomerSummaryDto dto = visitService.getCustomerSummary(getUserLogin(), id);
        return new Envelope(dto).toResponseEntity(HttpStatus.OK);
    }

    // FOR VISIT
    @RequestMapping(value = "/for-visit", method = RequestMethod.GET)
    public ResponseEntity<?> getCustomersForVisit(
    		@RequestParam(required = false) Boolean today,
            @RequestParam(value = "q", required = false) String search) {
        ListDto<CustomerForVisitDto> dtos = visitService.getCustomersForVisit(getUserLogin(), today, search);
        return new Envelope(dtos).toResponseEntity(HttpStatus.OK);
    }
    
    // UPDATE PHONE
    @RequestMapping(value = "/{id}/phone", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePhone(@PathVariable String id, @RequestBody StringDto wrapper) {
        visitService.updatePhone(getUserLogin(), id, wrapper.getContent());
        return new Envelope(Meta.OK).toResponseEntity(HttpStatus.OK);
    }

    // UPDATE MOBILE
    @RequestMapping(value = "/{id}/mobile", method = RequestMethod.PUT)
    public ResponseEntity<?> updateMobile(@PathVariable String id, @RequestBody StringDto wrapper) {
        visitService.updateMobile(getUserLogin(), id, wrapper.getContent());
        return new Envelope(Meta.OK).toResponseEntity(HttpStatus.OK);
    }

    // UPDATE LOCATION
    @RequestMapping(value = "/{id}/location", method = RequestMethod.PUT)
    public ResponseEntity<?> updateLocation(@PathVariable String id, @RequestBody Location dto) {
        visitService.updateLocation(getUserLogin(), id, dto);
        return new Envelope(Meta.OK).toResponseEntity(HttpStatus.OK);
    }
    
    // REGISTER
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<?> register(@RequestBody CustomerCreateDto dto) {
        IdDto domainId = customerCreatingService.registerCustomer(getUserLogin(), dto, false);
        return new Envelope(domainId).toResponseEntity(HttpStatus.CREATED);
    }

    // LIST CUSTOMER REGISTER BY SALES MAN
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ResponseEntity<?> listCustomerRegisteredBySalesMan(@RequestParam(required = false) String q,
            @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        ListDto<CustomerListDto> results = customerCreatingService.getCustomersRegistered(getUserLogin(), q,
                getPageRequest(page, size));
        return new Envelope(results).toResponseEntity(HttpStatus.OK);
    }

}
