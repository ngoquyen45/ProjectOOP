package com.viettel.backend.restful.supervisor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.common.IdDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.target.TargetCreateDto;
import com.viettel.backend.dto.target.TargetDto;
import com.viettel.backend.restful.AbstractController;
import com.viettel.backend.restful.Envelope;
import com.viettel.backend.restful.Meta;
import com.viettel.backend.service.target.TargetService;

@RestController
@RequestMapping(value = "/supervisor/target")
public class TargetController extends AbstractController {

    @Autowired
    private TargetService targetService;

    // LIST
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> list(@RequestParam(required = true) int month, @RequestParam(required = true) int year) {
        ListDto<TargetDto> results = targetService.getTargets(getUserLogin(), month, year);
        return new Envelope(results).toResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public ResponseEntity<?> detail(@RequestParam(required = true) int month, @RequestParam(required = true) int year,
            @RequestParam(required = true) String salesmanId) {
        TargetDto result = targetService.getTarget(getUserLogin(), month, year, salesmanId);
        return new Envelope(result).toResponseEntity();
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    public ResponseEntity<?> save(@RequestBody TargetCreateDto dto) {
        IdDto idDto = targetService.save(getUserLogin(), dto);
        return new Envelope(idDto).toResponseEntity();
    }

    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@RequestParam(required = true) int month, @RequestParam(required = true) int year,
            @RequestParam(required = true) String salesmanId) {
        targetService.delete(getUserLogin(), month, year, salesmanId);
        return new Envelope(Meta.OK).toResponseEntity();
    }

}
