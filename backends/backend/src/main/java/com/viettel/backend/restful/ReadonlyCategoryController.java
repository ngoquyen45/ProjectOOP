package com.viettel.backend.restful;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.viettel.backend.service.category.readonly.I_ReadonlyCategoryService;

public abstract class ReadonlyCategoryController extends AbstractController {

    @SuppressWarnings("rawtypes")
    protected abstract I_ReadonlyCategoryService getReadonlyCategoryService();

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public final ResponseEntity<?> getAll(@RequestParam(required = false) String distributorId) {
        if (getReadonlyCategoryService() == null) {
            return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
        }
        
        return new Envelope(getReadonlyCategoryService().getAll(getUserLogin(), distributorId))
                .toResponseEntity(HttpStatus.OK);
    }

}
