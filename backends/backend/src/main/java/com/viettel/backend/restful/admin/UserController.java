package com.viettel.backend.restful.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.category.UserCreateDto;
import com.viettel.backend.dto.category.UserDto;
import com.viettel.backend.dto.common.IdDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.common.CategorySeletionDto;
import com.viettel.backend.restful.AbstractController;
import com.viettel.backend.restful.Envelope;
import com.viettel.backend.restful.Meta;
import com.viettel.backend.service.category.editable.EditableUserService;

@RestController(value = "adminUserController")
@RequestMapping(value = "/admin/user")
public class UserController extends AbstractController {

    @Autowired
    private EditableUserService editableUserService;

    @RequestMapping(value = "", method = RequestMethod.POST)
    public final ResponseEntity<?> create(@RequestBody UserCreateDto dto) {
        IdDto domainId = editableUserService.create(getUserLogin(), dto);
        return new Envelope(domainId).toResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public final ResponseEntity<?> update(@PathVariable String id, @RequestBody UserCreateDto dto) {
        IdDto domainId = editableUserService.update(getUserLogin(), id, dto);
        return new Envelope(domainId).toResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public final ResponseEntity<?> detail(@PathVariable String id) {
        UserDto dto = editableUserService.getById(getUserLogin(), id);
        return new Envelope(dto).toResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/activate", method = RequestMethod.PUT)
    public final ResponseEntity<?> activate(@PathVariable String id) {
        editableUserService.setActive(getUserLogin(), id, true);
        return new Envelope(Meta.OK).toResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/deactivate", method = RequestMethod.PUT)
    public final ResponseEntity<?> deactivate(@PathVariable String id) {
        editableUserService.setActive(getUserLogin(), id, false);
        return new Envelope(Meta.OK).toResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public final ResponseEntity<?> delete(@PathVariable String id) {
        editableUserService.delete(getUserLogin(), id);
        return new Envelope(Meta.OK).toResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public final ResponseEntity<?> getList(@RequestParam(required = false) String role,
            @RequestParam(required = false) String distributorId,
            @RequestParam(value = "q", required = false) String search, @RequestParam(required = false) Boolean draft,
            @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Boolean active) {
        ListDto<UserDto> dtos = editableUserService.getList(getUserLogin(), search, active, draft, role, distributorId,
                getPageRequest(page, size));
        return new Envelope(dtos).toResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/enable", method = RequestMethod.POST)
    public final ResponseEntity<?> createAndEnable(@RequestBody UserCreateDto dto) {
        IdDto domainId = editableUserService.create(getUserLogin(), dto);
        editableUserService.enable(getUserLogin(), domainId.getId());
        return new Envelope(domainId).toResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/enable", method = RequestMethod.PUT)
    public final ResponseEntity<?> enable(@PathVariable String id) {
        editableUserService.enable(getUserLogin(), id);
        return new Envelope(Meta.OK).toResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/reset-password", method = RequestMethod.PUT)
    public final ResponseEntity<?> resetPassword(@PathVariable String id) {
        editableUserService.resetPassword(getUserLogin(), id);
        return new Envelope(Meta.OK).toResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/distributors", method = RequestMethod.GET)
    public final ResponseEntity<?> getDistributorOfObserver(@PathVariable String id) {
        ListDto<CategorySeletionDto> dtos = editableUserService.getDistributorOfObserver(getUserLogin(), id);
        return new Envelope(dtos).toResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/distributors", method = RequestMethod.PUT)
    public final ResponseEntity<?> updateDistributorSetForObserver(@PathVariable String id,
            @RequestBody ListDto<String> listDto) {
        List<String> distributorIds = listDto == null ? null : listDto.getList();
        editableUserService.updateDistributorSetForObserver(getUserLogin(), id, distributorIds);
        return new Envelope(Meta.OK).toResponseEntity(HttpStatus.OK);
    }

}
