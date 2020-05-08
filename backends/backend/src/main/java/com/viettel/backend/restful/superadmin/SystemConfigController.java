package com.viettel.backend.restful.superadmin;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.common.IdDto;
import com.viettel.backend.dto.config.SystemConfigDto;
import com.viettel.backend.restful.AbstractController;
import com.viettel.backend.restful.Envelope;
import com.viettel.backend.service.config.SystemConfigService;

@RestController(value="superAdminSystemConfigController")
@RequestMapping(value = "/super-admin/system-config")
public class SystemConfigController extends AbstractController {

	@Autowired
	private SystemConfigService systemConfigService;
	
    // SET
    @RequestMapping(value = "", method = RequestMethod.PUT)
    public ResponseEntity<?> update(@RequestBody @Valid SystemConfigDto dto) {
        IdDto id = systemConfigService.set(getUserLogin(), dto);
        return new Envelope(id).toResponseEntity(HttpStatus.OK); 
    }

    // GET
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> detail() {
        SystemConfigDto dto = systemConfigService.get(getUserLogin());
    	return new Envelope(dto).toResponseEntity(HttpStatus.OK); 
    }
    
}
