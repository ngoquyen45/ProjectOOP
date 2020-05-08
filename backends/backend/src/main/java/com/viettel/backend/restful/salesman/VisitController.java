package com.viettel.backend.restful.salesman;

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

import com.viettel.backend.dto.common.IdDto;
import com.viettel.backend.dto.visit.VisitClosingDto;
import com.viettel.backend.dto.visit.VisitEndDto;
import com.viettel.backend.dto.visit.VisitInfoDto;
import com.viettel.backend.dto.visit.VisitStartDto;
import com.viettel.backend.restful.AbstractController;
import com.viettel.backend.restful.Envelope;
import com.viettel.backend.service.visit.VisitService;

@RestController(value = "salesmanVisitController")
@RequestMapping(value = "/salesman/visit")
public class VisitController extends AbstractController {
    
    @Autowired
    private VisitService visitService;
    
    /** START VISIT */
    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public ResponseEntity<?> startVisit(@RequestParam(required = true) String customerId,
            @RequestBody VisitStartDto dto) {
        IdDto visitId = visitService.startVisit(getUserLogin(), customerId, dto.getLocation());
        return new Envelope(visitId).toResponseEntity(HttpStatus.OK);
    }
    
    /** END VISIT */
    @RequestMapping(value = "/{id}/end", method = RequestMethod.PUT)
    public ResponseEntity<?> endVisit(@PathVariable String id, @RequestBody @Valid VisitEndDto dto) {
        VisitInfoDto visitInfoDto = visitService.endVisit(getUserLogin(), id, dto);
        return new Envelope(visitInfoDto).toResponseEntity(HttpStatus.OK);
    }
    
    /** MARK AS CLOSED */
    @RequestMapping(value = "/close", method = RequestMethod.POST)
    public ResponseEntity<?> markAsClosed(@RequestParam(required = true) String customerId,
            @RequestBody VisitClosingDto dto) {
        IdDto visitId = visitService.markAsClosed(getUserLogin(), customerId, dto);
        return new Envelope(visitId).toResponseEntity(HttpStatus.OK);
    }
    
    /** VISIT INFO */
    @RequestMapping(value = "/today", method = RequestMethod.GET)
    public ResponseEntity<?> getVisitInfo(@RequestParam String customerId) {
        VisitInfoDto dto = visitService.getVisitedTodayInfo(getUserLogin(), customerId);
        return new Envelope(dto).toResponseEntity(HttpStatus.OK);
    }
    
}
