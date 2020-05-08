package com.viettel.backend.restful.superadmin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.common.CategoryDto;
import com.viettel.backend.dto.config.ClientCreateDto;
import com.viettel.backend.dto.config.ClientDto;
import com.viettel.backend.restful.EditableCategoryController;
import com.viettel.backend.restful.Envelope;
import com.viettel.backend.restful.Meta;
import com.viettel.backend.service.category.editable.I_EditableCategoryService;
import com.viettel.backend.service.config.ClientService;

@RestController(value="superAdminClientController")
@RequestMapping(value = "/super-admin/client")
public class ClientController extends
        EditableCategoryController<CategoryDto, ClientDto, ClientCreateDto> {

    @Autowired
    private ClientService clientService;

    @Override
    protected I_EditableCategoryService<CategoryDto, ClientDto, ClientCreateDto> getEditableService() {
        return clientService;
    }
    
    @Override
    protected boolean canUpdate() {
        return true;
    }
    
    @Override
    protected boolean canDelete() {
        return false;
    }
    
    @Override
    protected boolean canEnable() {
        return false;
    }
    
    @Override
    protected boolean canSetActive() {
        return false;
    }
    
    @RequestMapping(value = "/create-sample-master-data", method = RequestMethod.POST)
    public ResponseEntity<?> createSampleMasterData(@RequestParam(required = true) String clientId) {
        clientService.createSampleMasterData(getUserLogin(), clientId);
        return new Envelope(Meta.OK).toResponseEntity(HttpStatus.OK);
    }
    
    @RequestMapping(value = "/generate-visit-and-order", method = RequestMethod.POST)
    public ResponseEntity<?> generateVisitAndOrder(@RequestParam(required = true) String clientId) {
        clientService.generateVisitAndOrder(getUserLogin(), clientId);
        return new Envelope(Meta.OK).toResponseEntity(HttpStatus.OK);
    }

}
