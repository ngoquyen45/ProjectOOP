package com.viettel.backend.restful.admin;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.common.IdDto;
import com.viettel.backend.dto.config.ClientConfigDto;
import com.viettel.backend.restful.AbstractController;
import com.viettel.backend.restful.Envelope;
import com.viettel.backend.service.config.ClientConfigService;

@RestController(value = "adminClientConfigController")
@RequestMapping(value = "/admin/client-config")
public class ClientConfigController extends AbstractController {

    @Autowired
    private ClientConfigService clientConfigService;

    // SET
    @RequestMapping(value = "", method = RequestMethod.PUT)
    public ResponseEntity<?> update(@RequestBody @Valid ClientConfigDto dto) {
        IdDto id = clientConfigService.set(getUserLogin(), dto);
        return new Envelope(id).toResponseEntity(HttpStatus.OK);
    }

    // GET
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> detail() {
        ClientConfigDto dto = clientConfigService.get(getUserLogin());
        return new Envelope(dto).toResponseEntity(HttpStatus.OK);
    }

}
