package com.viettel.backend.restful.distributor;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.category.SurveyListDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.report.DistributorSalesResultDto;
import com.viettel.backend.dto.report.DistributorVisitResultDto;
import com.viettel.backend.dto.report.ProductSalesResultDto;
import com.viettel.backend.dto.report.SalesResultDailyDto;
import com.viettel.backend.dto.report.SalesmanSalesResultDto;
import com.viettel.backend.dto.report.SalesmanVisitResultDto;
import com.viettel.backend.dto.report.SurveyResultDto;
import com.viettel.backend.dto.report.VisitResultDailyDto;
import com.viettel.backend.restful.AbstractController;
import com.viettel.backend.restful.Envelope;
import com.viettel.backend.service.report.SalesReportService;
import com.viettel.backend.service.report.SurveyReportService;
import com.viettel.backend.service.report.VisitReportService;

@RestController(value = "distributorReportController")
@RequestMapping(value = "/distributor/report")
public class ReportController extends AbstractController {

    @Autowired
    private SalesReportService salesReportService;

    @Autowired
    private VisitReportService visitReportService;

    @Autowired
    private SurveyReportService surveyReportService;

    // SALES
    @RequestMapping(value = "/sales/daily", method = RequestMethod.GET)
    public final ResponseEntity<?> getSalesResultDaily(@RequestParam(required = true) int month,
            @RequestParam(required = true) int year) {
        ListDto<SalesResultDailyDto> list = salesReportService.getSalesResultDaily(getUserLogin(), month, year);
        return new Envelope(list).toResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/sales/by-distributor", method = RequestMethod.GET)
    public final ResponseEntity<?> getDistributorSalesResult(@RequestParam(required = true) int month,
            @RequestParam(required = true) int year) {
        ListDto<DistributorSalesResultDto> list = salesReportService.getDistributorSalesResult(getUserLogin(), month,
                year);
        return new Envelope(list).toResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/sales/by-product", method = RequestMethod.GET)
    public final ResponseEntity<?> getProductSalesResult(@RequestParam(required = true) int month,
            @RequestParam(required = true) int year, @RequestParam(required = true) String productCategoryId) {
        ListDto<ProductSalesResultDto> list = salesReportService.getProductSalesResult(getUserLogin(), month, year,
                productCategoryId);
        return new Envelope(list).toResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/sales/by-salesman", method = RequestMethod.GET)
    public final ResponseEntity<?> getSalesmanSalesResult(@RequestParam(required = true) int month,
            @RequestParam(required = true) int year) {
        ListDto<SalesmanSalesResultDto> list = salesReportService.getSalesmanSalesResult(getUserLogin(), month, year);
        return new Envelope(list).toResponseEntity(HttpStatus.OK);
    }

    // VISIT
    @RequestMapping(value = "/visit/daily", method = RequestMethod.GET)
    public final ResponseEntity<?> getVisitResultDaily(@RequestParam(required = true) int month,
            @RequestParam(required = true) int year) {
        ListDto<VisitResultDailyDto> list = visitReportService.getVisitResultDaily(getUserLogin(), month, year);
        return new Envelope(list).toResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/visit/by-distributor", method = RequestMethod.GET)
    public final ResponseEntity<?> getDistributorVisitResult(@RequestParam(required = true) int month,
            @RequestParam(required = true) int year) {
        ListDto<DistributorVisitResultDto> list = visitReportService.getDistributorVisitResult(getUserLogin(), month,
                year);
        return new Envelope(list).toResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/visit/by-salesman", method = RequestMethod.GET)
    public final ResponseEntity<?> getSalesmanVisitResult(@RequestParam(required = true) int month,
            @RequestParam(required = true) int year) {
        ListDto<SalesmanVisitResultDto> list = visitReportService.getSalesmanVisitResult(getUserLogin(), month, year);
        return new Envelope(list).toResponseEntity(HttpStatus.OK);
    }

    // SURVEY
    @RequestMapping(value = "/survey", method = RequestMethod.GET)
    public ResponseEntity<?> getSurveyReport(@RequestParam(required = false) String search,
            @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        ListDto<SurveyListDto> list = surveyReportService.getSurveys(getUserLogin(), search,
                getPageRequest(page, size));
        return new Envelope(list).toResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/survey/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getSurveyReport(@PathVariable String id) {
        SurveyResultDto dto = surveyReportService.getSurveyReport(getUserLogin(), id);
        return new Envelope(dto).toResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/survey/{id}/export", method = RequestMethod.GET)
    public ResponseEntity<?> exportSurveyResult(@PathVariable String id, @RequestParam String lang) throws IOException {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        header.set("Content-Disposition", "attachment; filename=\"Survey.xlsx\"");
        ResponseEntity<byte[]> result = new ResponseEntity<byte[]>(
                surveyReportService.exportSurveyReport(getUserLogin(), id, lang), header, HttpStatus.OK);
        return result;
    }

}
