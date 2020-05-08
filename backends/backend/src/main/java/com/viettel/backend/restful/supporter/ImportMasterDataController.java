package com.viettel.backend.restful.supporter;

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

import com.viettel.backend.dto.imp.ImportConfirmDto;
import com.viettel.backend.restful.AbstractController;
import com.viettel.backend.restful.Envelope;
import com.viettel.backend.restful.Meta;
import com.viettel.backend.service.imp.ImportMasterDataService;

@RestController(value = "supporterImportMasterDataController")
@RequestMapping(value = "/supporter/import/master-data")
public class ImportMasterDataController extends AbstractController {

    @Autowired
    private ImportMasterDataService importMasterDataService;

    @RequestMapping(value = "/template", method = RequestMethod.GET)
    public ResponseEntity<?> getMasterDataTemplate(@RequestParam String lang) {
        InputStream inputStream = importMasterDataService.getMasterDataTemplate(lang);
        InputStreamResource response = new InputStreamResource(inputStream);

        String filename = "MasterData.xlsx";

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        header.set("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        return new ResponseEntity<InputStreamResource>(response, header, HttpStatus.OK);
    }

    @RequestMapping(value = "/verify", method = RequestMethod.GET)
    public ResponseEntity<?> verify(@RequestParam(required = true) String fileId) {
        ImportConfirmDto dto = importMasterDataService.verify(getUserLogin(), fileId);
        return new Envelope(dto).toResponseEntity(HttpStatus.OK);
    }
    
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public ResponseEntity<?> importCustomer(@RequestParam(required = true) String clientId,
            @RequestParam(required = true) String fileId) {
        importMasterDataService.importMasterData(getUserLogin(), clientId, fileId);
        return new Envelope(Meta.OK).toResponseEntity(HttpStatus.OK);
    }

}
