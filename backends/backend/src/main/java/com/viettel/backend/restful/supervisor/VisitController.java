package com.viettel.backend.restful.supervisor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.visit.CustomerForVisitDto;
import com.viettel.backend.dto.visit.VisitInfoDto;
import com.viettel.backend.dto.visit.VisitInfoListDto;
import com.viettel.backend.dto.visit.VisitTodaySummary;
import com.viettel.backend.restful.AbstractController;
import com.viettel.backend.restful.Envelope;
import com.viettel.backend.service.visit.VisitMonitoringService;

@RestController(value = "supervisorVisitController")
@RequestMapping(value = "/supervisor/visit")
public class VisitController extends AbstractController {

    @Autowired
    private VisitMonitoringService visitMonitoringService;
    
    @RequestMapping(value = "", method = RequestMethod.GET)
    public final ResponseEntity<?> getVisitsToday(@RequestParam(required = false) String distributorId,
            @RequestParam(required = false) String salesmanId, @RequestParam(required = true) String fromDate,
            @RequestParam(required = true) String toDate, @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        ListDto<VisitInfoListDto> dtos = visitMonitoringService.getVisits(getUserLogin(), distributorId,
                salesmanId, fromDate, toDate, getPageRequest(page, size));
        return new Envelope(dtos).toResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/today", method = RequestMethod.GET)
    public final ResponseEntity<?> getVisitsToday(@RequestParam(required = false) String distributorId, @RequestParam(
            required = false) String salesmanId, @RequestParam(required = false) Integer page, @RequestParam(
            required = false) Integer size) {
        ListDto<VisitInfoListDto> dtos = visitMonitoringService.getVisitsToday(getUserLogin(), distributorId, salesmanId,
                getPageRequest(page, size));
        return new Envelope(dtos).toResponseEntity(HttpStatus.OK);
    }
    
    @RequestMapping(value = "/today/summary", method = RequestMethod.GET)
    public final ResponseEntity<?> getVisitTodaySummary(@RequestParam(required = false) String distributorId,
            @RequestParam(required = false) String salesmanId) {
        VisitTodaySummary visitTodaySummary = visitMonitoringService.getVisitTodaySummary(getUserLogin(), distributorId,
                salesmanId);
        return new Envelope(visitTodaySummary).toResponseEntity(HttpStatus.OK);
    }
    
    @RequestMapping(value = "/today/by-salesman", method = RequestMethod.GET)
    public final ResponseEntity<?> getVisitsToday( @RequestParam(required = true) String salesmanId) {
        ListDto<CustomerForVisitDto> dtos = visitMonitoringService.getCustomersTodayBySalesman(getUserLogin(), salesmanId);
        return new Envelope(dtos).toResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public final ResponseEntity<?> getVisitInfoById(@PathVariable String id) {
        VisitInfoDto dto = visitMonitoringService.getVisitInfoById(getUserLogin(), id);
        return new Envelope(dto).toResponseEntity(HttpStatus.OK);
    }

}
