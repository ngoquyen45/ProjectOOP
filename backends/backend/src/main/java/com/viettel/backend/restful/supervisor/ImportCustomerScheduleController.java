package com.viettel.backend.restful.supervisor;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.imp.ImportConfirmDto;
import com.viettel.backend.dto.imp.ImportResultDto;
import com.viettel.backend.restful.AbstractController;
import com.viettel.backend.restful.Envelope;
import com.viettel.backend.service.imp.ImportCustomerScheduleService;

@RestController(value = "supervisorImportCustomerScheduleController")
@RequestMapping(value = "/supervisor/import/customer-schedule")
public class ImportCustomerScheduleController extends AbstractController {

    @Autowired
    private ImportCustomerScheduleService importCustomerScheduleService;

    @RequestMapping(value = "/template", method = RequestMethod.GET)
    public ResponseEntity<?> exportSurveyResult(@RequestParam(required = true) String distributorId,
            @RequestParam String lang) throws IOException {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        header.set("Content-Disposition", "attachment; filename=\"CustomerSchedule.xlsx\"");
        ResponseEntity<byte[]> result = new ResponseEntity<byte[]>(
                importCustomerScheduleService.getTemplate(getUserLogin(), distributorId, lang), header, HttpStatus.OK);
        return result;
    }

    @RequestMapping(value = "/verify", method = RequestMethod.GET)
    public ResponseEntity<?> verify(@RequestParam(required = true) String distributorId,
            @RequestParam(required = true) String fileId) {
        ImportConfirmDto dto = importCustomerScheduleService.verify(getUserLogin(), distributorId, fileId);
        return new Envelope(dto).toResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public ResponseEntity<?> importCustomer(@RequestParam(required = true) String distributorId,
            @RequestParam(required = true) String fileId) {
        ImportResultDto dto = importCustomerScheduleService.doImport(getUserLogin(), distributorId, fileId);
        return new Envelope(dto).toResponseEntity(HttpStatus.OK);
    }

}
