package com.viettel.backend.restful.superadmin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.restful.Envelope;
import com.viettel.backend.restful.Meta;
import com.viettel.backend.service.system.CacheInitiator;

@RestController(value="superAdminSystemController")
@RequestMapping(value = "/super-admin/system")
public class SystemController {

    @Autowired
    private CacheInitiator cacheInitiator;
    
    @RequestMapping(value = "/reset-cache", method = RequestMethod.PUT)
    public ResponseEntity<?> resetCache() {
        cacheInitiator.resetCache();
        return new Envelope(Meta.OK).toResponseEntity(HttpStatus.OK); 
    }
    
}
