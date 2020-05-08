package com.viettel.backend.restful.distributor;

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
import com.viettel.backend.service.imp.ImportPriceListService;

@RestController(value = "distributorImportPriceListController")
@RequestMapping(value = "/distributor/import/price-list")
public class ImportPriceListController extends AbstractController {

    @Autowired
    private ImportPriceListService importPriceListService;

    @RequestMapping(value = "/template", method = RequestMethod.GET)
    public ResponseEntity<?> getTemplate(@RequestParam String lang) throws IOException {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        header.set("Content-Disposition", "attachment; filename=\"ProductPriceList.xlsx\"");
        ResponseEntity<byte[]> result = new ResponseEntity<byte[]>(
                importPriceListService.getTemplate(getUserLogin(), null, lang), header, HttpStatus.OK);
        return result;
    }

    @RequestMapping(value = "/verify", method = RequestMethod.GET)
    public ResponseEntity<?> verify(@RequestParam(required = true) String fileId) {
        ImportConfirmDto dto = importPriceListService.verify(getUserLogin(), null, fileId);
        return new Envelope(dto).toResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public ResponseEntity<?> doImport(@RequestParam(required = true) String fileId) {
        ImportResultDto dto = importPriceListService.doImport(getUserLogin(), null, fileId);
        return new Envelope(dto).toResponseEntity(HttpStatus.OK);
    }

}
