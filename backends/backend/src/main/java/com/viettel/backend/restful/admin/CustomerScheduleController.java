package com.viettel.backend.restful.admin;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.schedule.CustomerScheduleCreateDto;
import com.viettel.backend.dto.schedule.CustomerScheduleDto;
import com.viettel.backend.restful.AbstractController;
import com.viettel.backend.restful.Envelope;
import com.viettel.backend.restful.Meta;
import com.viettel.backend.service.schedule.CustomerScheduleService;

@RestController(value = "adminCustomerScheduleController")
@RequestMapping(value = "/admin/customer-schedule")
public class CustomerScheduleController extends AbstractController {

    @Autowired
    private CustomerScheduleService customerScheduleService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public final ResponseEntity<?> getCustomerSchedules(@RequestParam(required = false) String distributorId,
            @RequestParam boolean searchByRoute, @RequestParam(required = false) String routeId,
            @RequestParam(value = "q", required = false) String search, @RequestParam(required = false) Integer day,
            @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        ListDto<CustomerScheduleDto> dtos = customerScheduleService.getCustomerSchedules(getUserLogin(), distributorId,
                searchByRoute, routeId, search, day, getPageRequest(page, size));
        return new Envelope(dtos).toResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public final ResponseEntity<?> getCustomerSchedule(@PathVariable String id) {
        CustomerScheduleDto dto = customerScheduleService.getCustomerSchedule(getUserLogin(), id);
        return new Envelope(dto).toResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/by-distributor", method = RequestMethod.PUT)
    public final ResponseEntity<?> saveCustomerScheduleByDistributor(
            @RequestParam(required = false) String distributorId,
            @RequestBody @Valid ListDto<CustomerScheduleCreateDto> list) {
        customerScheduleService.saveCustomerScheduleByDistributor(getUserLogin(), distributorId, list);
        return new Envelope(Meta.OK).toResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public final ResponseEntity<?>
            saveCustomerScheduleByDistributor(@RequestBody @Valid CustomerScheduleCreateDto dto) {
        customerScheduleService.saveCustomerSchedule(getUserLogin(), dto);
        return new Envelope(Meta.OK).toResponseEntity(HttpStatus.OK);
    }

}
