package com.viettel.backend.restful.supervisor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.category.UserSimpleDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.restful.AbstractController;
import com.viettel.backend.restful.Envelope;
import com.viettel.backend.service.category.readonly.UserService;

@RestController(value="supervisorSalesmanController")
@RequestMapping(value = "/supervisor/salesman")
public class SalesmanController extends AbstractController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public final ResponseEntity<?> getList(@RequestParam(required = false) String distributorId) {
        ListDto<UserSimpleDto> dtos = userService.getSalesmen(getUserLogin(), distributorId);
        return new Envelope(dtos).toResponseEntity(HttpStatus.OK);
    }

}
