package com.viettel.backend.restful.supervisor;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.restful.AbstractController;
import com.viettel.backend.service.exp.ExchangeReturnExportService;
import com.viettel.backend.service.exp.OrderExportService;

@RestController(value = "supervisorExportController")
@RequestMapping(value = "/supervisor/export")
public class ExportController extends AbstractController {

    @Autowired
    private OrderExportService orderExportService;
    
    @Autowired
    private ExchangeReturnExportService exchangeReturnExportService;
    
    @RequestMapping(value = "/order", method = RequestMethod.GET)
    public ResponseEntity<?> exportOrder(@RequestParam String distributorId, @RequestParam String fromDate,
            @RequestParam String toDate, @RequestParam String lang) throws IOException {
        InputStream inputStream = orderExportService.exportOrder(getUserLogin(), distributorId, fromDate, toDate, lang);
        InputStreamResource response = new InputStreamResource(inputStream);

        String filename = "OrderList_" + fromDate + "_" + toDate + ".xlsx";

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        header.set("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        return new ResponseEntity<InputStreamResource>(response, header, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/order/detail", method = RequestMethod.GET)
    public ResponseEntity<?> exportOrderDetail(@RequestParam String distributorId, @RequestParam String fromDate,
            @RequestParam String toDate, @RequestParam String lang) throws IOException {
        InputStream inputStream = orderExportService.exportOrderDetail(getUserLogin(), distributorId, fromDate, toDate,
                lang);
        InputStreamResource response = new InputStreamResource(inputStream);

        String filename = "OrderDetailList_" + fromDate + "_" + toDate + ".xlsx";

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        header.set("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        return new ResponseEntity<InputStreamResource>(response, header, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/exchange-return", method = RequestMethod.GET)
    public ResponseEntity<?> exportExchangeReturn(@RequestParam(required=false) String distributorId, @RequestParam String fromDate,
            @RequestParam String toDate, @RequestParam String lang) throws IOException {
        InputStream inputStream = exchangeReturnExportService.export(getUserLogin(), distributorId, fromDate, toDate,
                lang);
        InputStreamResource response = new InputStreamResource(inputStream);

        String filename = "ExchangeReturn_" + fromDate + "_" + toDate + ".xlsx";

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        header.set("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        return new ResponseEntity<InputStreamResource>(response, header, HttpStatus.OK);
    }

}
