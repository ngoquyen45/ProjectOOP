package com.viettel.backend.restful.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.imp.ImportConfirmDto;
import com.viettel.backend.dto.imp.ImportProductPhotoDto;
import com.viettel.backend.dto.imp.ImportResultDto;
import com.viettel.backend.restful.AbstractController;
import com.viettel.backend.restful.Envelope;
import com.viettel.backend.service.imp.ImportProductService;

@RestController(value = "ImportProductController")
@RequestMapping(value = "/admin/import/product")
public class ImportProductController extends AbstractController {

    @Autowired
    private ImportProductService importProductService;

    @RequestMapping(value = "/template", method = RequestMethod.GET)
    public ResponseEntity<?> getImportCustomerTemplate(@RequestParam String lang) {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        header.set("Content-Disposition", "attachment; filename=\"Product.xlsx\"");
        ResponseEntity<byte[]> result = new ResponseEntity<byte[]>(
                importProductService.getImportProductTemplate(getUserLogin(), lang), header, HttpStatus.OK);
        return result;
    }

    @RequestMapping(value = "/verify", method = RequestMethod.GET)
    public ResponseEntity<?> verify(@RequestParam(required = true) String fileId) {
        ImportConfirmDto dto = importProductService.verify(getUserLogin(), fileId);
        return new Envelope(dto).toResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/confirm", method = RequestMethod.GET)
    public ResponseEntity<?> confirm(@RequestParam(required = true) String fileId) {
        ImportConfirmDto dto = importProductService.confirm(getUserLogin(), fileId);
        return new Envelope(dto).toResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public ResponseEntity<?> importCustomer(@RequestParam(required = true) String fileId,
            @RequestBody ImportProductPhotoDto photos) {
        ImportResultDto dto = importProductService.importProduct(getUserLogin(), fileId, photos);
        return new Envelope(dto).toResponseEntity(HttpStatus.OK);
    }

}
