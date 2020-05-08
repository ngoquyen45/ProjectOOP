package com.viettel.backend.restful.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.category.UserSimpleDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.restful.AbstractController;
import com.viettel.backend.restful.Envelope;
import com.viettel.backend.service.category.readonly.UserService;

@RestController(value="adminSupervisorController")
@RequestMapping(value = "/admin/supervisor")
public class SupervisorController extends AbstractController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public final ResponseEntity<?> getList() {
        ListDto<UserSimpleDto> dtos = userService.getSupervisors(getUserLogin());
        return new Envelope(dtos).toResponseEntity(HttpStatus.OK);
    }

}
