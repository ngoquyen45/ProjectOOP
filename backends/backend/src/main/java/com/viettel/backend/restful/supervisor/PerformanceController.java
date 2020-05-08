package com.viettel.backend.restful.supervisor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.report.PerformanceDto;
import com.viettel.backend.restful.AbstractController;
import com.viettel.backend.restful.Envelope;
import com.viettel.backend.service.report.PerformanceService;

@RestController(value = "supervisorPerformanceController")
@RequestMapping(value = "/supervisor/performance")
public class PerformanceController extends AbstractController {

    @Autowired
    private PerformanceService performanceService;

    @RequestMapping(value = "/by-salesman", method = RequestMethod.GET)
    public ResponseEntity<?> getPerformanceBySalesman(@RequestParam(required = true) String salesmanId,
            @RequestParam(required = true) int month, @RequestParam(required = true) int year) {
        PerformanceDto dto = performanceService.getPerformanceBySalesman(getUserLogin(), salesmanId, month, year);
        return new Envelope(dto).toResponseEntity();
    }

}
