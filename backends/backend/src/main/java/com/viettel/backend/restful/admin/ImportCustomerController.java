package com.viettel.backend.restful.admin;

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
import com.viettel.backend.service.imp.ImportCustomerService;

@RestController(value = "adminImportCustomerController")
@RequestMapping(value = "/admin/import/customer")
public class ImportCustomerController extends AbstractController {

    @Autowired
    private ImportCustomerService importCustomerService;

    @RequestMapping(value = "/template", method = RequestMethod.GET)
    public ResponseEntity<?> getImportCustomerTemplate(@RequestParam(required = true) String distributorId,
            @RequestParam String lang) {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        header.set("Content-Disposition", "attachment; filename=\"Customer.xlsx\"");
        ResponseEntity<byte[]> result = new ResponseEntity<byte[]>(
                importCustomerService.getImportCustomerTemplate(getUserLogin(), distributorId, lang), header,
                HttpStatus.OK);
        return result;
    }

    @RequestMapping(value = "/verify", method = RequestMethod.GET)
    public ResponseEntity<?> verify(@RequestParam(required = true) String distributorId,
            @RequestParam(required = true) String fileId) {
        ImportConfirmDto dto = importCustomerService.verify(getUserLogin(), distributorId, fileId);
        return new Envelope(dto).toResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public ResponseEntity<?> importCustomer(@RequestParam(required = true) String distributorId,
            @RequestParam(required = true) String fileId) {
        ImportResultDto dto = importCustomerService.importCustomer(getUserLogin(), distributorId, fileId);
        return new Envelope(dto).toResponseEntity(HttpStatus.OK);
    }

}
