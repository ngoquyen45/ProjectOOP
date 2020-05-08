package com.viettel.backend.restful.salesman;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.order.OrderSimpleDto;
import com.viettel.backend.dto.report.CustomerSalesResultDto;
import com.viettel.backend.dto.report.MobileDashboardDto;
import com.viettel.backend.dto.report.SalesResultDailyDto;
import com.viettel.backend.restful.AbstractController;
import com.viettel.backend.restful.Envelope;
import com.viettel.backend.service.report.MobileDashboardService;

@RestController(value = "salesmanDashboardController")
@RequestMapping(value = "/salesman/dashboard")
public class DashboardController extends AbstractController {

    @Autowired
    private MobileDashboardService mobileDashboardService;

    // GET DASHBOARD
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getDashboard() {
        MobileDashboardDto dto = mobileDashboardService.getMobileDashboard(getUserLogin());
        return new Envelope(dto).toResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/by-customer", method = RequestMethod.GET)
    public ResponseEntity<?> getDashboardByCustomer() {
        ListDto<CustomerSalesResultDto> dtos = mobileDashboardService.getCustomerSalesResultsThisMonth(getUserLogin());
        return new Envelope(dtos).toResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/by-customer/detail", method = RequestMethod.GET)
    public ResponseEntity<?>
            getDashboardByCustomerDetail(@RequestParam(required = true) String customerId) {
        ListDto<OrderSimpleDto> dtos = mobileDashboardService.getOrderByCustomerThisMonth(getUserLogin(), customerId);
        return new Envelope(dtos).toResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/by-day", method = RequestMethod.GET)
    public ResponseEntity<?> getDashboardByDay() {
        ListDto<SalesResultDailyDto> dtos = mobileDashboardService.getSalesResultDailyThisMonth(getUserLogin());
        return new Envelope(dtos).toResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/by-day/detail", method = RequestMethod.GET)
    public ResponseEntity<?> getDashboardByDayDetail(@RequestParam(value = "date", required = true) String date) {
        ListDto<OrderSimpleDto> dtos = mobileDashboardService.getOrderByDateThisMonth(getUserLogin(), date);
        return new Envelope(dtos).toResponseEntity(HttpStatus.OK);
    }

}
