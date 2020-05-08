package com.viettel.backend.restful.admin;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.common.IdDto;
import com.viettel.backend.dto.config.CalendarConfigDto;
import com.viettel.backend.restful.AbstractController;
import com.viettel.backend.restful.Envelope;
import com.viettel.backend.service.config.CalendarConfigService;

@RestController(value = "adminCalendarConfigController")
@RequestMapping(value = "/admin/calendar-config")
public class CalendarConfigController extends AbstractController {

    @Autowired
    private CalendarConfigService calendarConfigService;

    // SET
    @RequestMapping(value = "", method = RequestMethod.PUT)
    public ResponseEntity<?> update(@RequestBody @Valid CalendarConfigDto dto) {
        IdDto id = calendarConfigService.set(getUserLogin(), dto);
        return new Envelope(id).toResponseEntity(HttpStatus.OK);
    }

    // GET
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> detail() {
        CalendarConfigDto dto = calendarConfigService.get(getUserLogin());
        return new Envelope(dto).toResponseEntity(HttpStatus.OK);
    }

}
