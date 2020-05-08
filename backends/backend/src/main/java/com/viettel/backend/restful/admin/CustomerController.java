package com.viettel.backend.restful.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.category.CustomerCreateDto;
import com.viettel.backend.dto.category.CustomerDto;
import com.viettel.backend.dto.category.CustomerListDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.restful.EditableCategoryController;
import com.viettel.backend.restful.Envelope;
import com.viettel.backend.restful.Meta;
import com.viettel.backend.service.category.editable.EditableCustomerService;
import com.viettel.backend.service.category.editable.I_EditableCategoryService;
import com.viettel.backend.service.customer.CustomerApprovalService;

@RestController(value = "adminCustomerController")
@RequestMapping(value = "/admin/customer")
public class CustomerController
        extends EditableCategoryController<CustomerListDto, CustomerDto, CustomerCreateDto> {

    @Autowired
    private EditableCustomerService editableCustomerService;

    @Autowired
    private CustomerApprovalService customerApprovalService;

    @Override
    protected I_EditableCategoryService<CustomerListDto, CustomerDto, CustomerCreateDto> getEditableService() {
        return editableCustomerService;
    }

    @RequestMapping(value = "/pending", method = RequestMethod.GET)
    public final ResponseEntity<?> getPendingOrders(@RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        ListDto<CustomerListDto> dtos = customerApprovalService.getPendingCustomers(getUserLogin(),
                getPageRequest(page, size));
        return new Envelope(dtos).toResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/pending/{id}", method = RequestMethod.GET)
    public final ResponseEntity<?> getOrderPendingById(@PathVariable String id) {
        CustomerListDto dto = customerApprovalService.getCustomerById(getUserLogin(), id);
        return new Envelope(dto).toResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/pending/{id}/approve", method = RequestMethod.PUT)
    public final ResponseEntity<?> approve(@PathVariable String id) {
        customerApprovalService.approve(getUserLogin(), id);
        return new Envelope(Meta.OK).toResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/pending/{id}/reject", method = RequestMethod.PUT)
    public final ResponseEntity<?> reject(@PathVariable String id) {
        customerApprovalService.reject(getUserLogin(), id);
        return new Envelope(Meta.OK).toResponseEntity(HttpStatus.OK);
    }

}
