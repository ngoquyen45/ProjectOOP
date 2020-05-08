package com.viettel.backend.restful.distributor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.report.WebDashboardDto;
import com.viettel.backend.restful.AbstractController;
import com.viettel.backend.restful.Envelope;
import com.viettel.backend.service.report.WebDashboardService;

@RestController(value = "distributorDashboardController")
@RequestMapping(value = "/distributor/dashboard")
public class DashboardController extends AbstractController {

    @Autowired
    private WebDashboardService dashboardService;
    
    @RequestMapping(value = "", method = RequestMethod.GET)
    public final ResponseEntity<?> getWebDashboard() {
        WebDashboardDto dto = dashboardService.getWebDashboard(getUserLogin());
        return new Envelope(dto).toResponseEntity(HttpStatus.OK);
    }

}
