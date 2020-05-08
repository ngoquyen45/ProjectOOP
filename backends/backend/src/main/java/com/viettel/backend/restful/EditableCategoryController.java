package com.viettel.backend.restful;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.viettel.backend.dto.common.CategoryCreateDto;
import com.viettel.backend.dto.common.CategoryDto;
import com.viettel.backend.dto.common.IdDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.service.category.editable.I_EditableCategoryService;
import com.viettel.backend.service.category.readonly.I_ReadonlyCategoryService;

public abstract class EditableCategoryController<LIST_SIMPLE_DTO extends CategoryDto, LIST_DETAIL_DTO extends LIST_SIMPLE_DTO, CREATE_DTO extends CategoryCreateDto>
        extends ReadonlyCategoryController {

    protected abstract I_EditableCategoryService<LIST_SIMPLE_DTO, LIST_DETAIL_DTO, CREATE_DTO> getEditableService();

    @SuppressWarnings("rawtypes")
    protected I_ReadonlyCategoryService getReadonlyCategoryService() {
        return null;
    }
    
    @RequestMapping(value = "", method = RequestMethod.POST)
    public final ResponseEntity<?> create(@RequestBody CREATE_DTO dto) {
        return _create(dto);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public final ResponseEntity<?> update(@PathVariable String id, @RequestBody CREATE_DTO dto) {
        return _update(id, dto);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public final ResponseEntity<?> detail(@PathVariable String id) {
        return _detail(id);
    }

    @RequestMapping(value = "/{id}/activate", method = RequestMethod.PUT)
    public final ResponseEntity<?> activate(@PathVariable String id) {
        return _setActive(id, true);
    }

    @RequestMapping(value = "/{id}/deactivate", method = RequestMethod.PUT)
    public final ResponseEntity<?> deactivate(@PathVariable String id) {
        return _setActive(id, false);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public final ResponseEntity<?> delete(@PathVariable String id) {
        return _delete(id);
    }

    // LIST
    @RequestMapping(value = "", method = RequestMethod.GET)
    public final ResponseEntity<?> getList(@RequestParam(required = false) String distributorId, @RequestParam(
            value = "q", required = false) String search, @RequestParam(required = false) Boolean draft, @RequestParam(
            required = false) Integer page, @RequestParam(required = false) Integer size, @RequestParam(
            required = false) Boolean active) {
        return _getList(search, active, draft, distributorId, getPageRequest(page, size));
    }
    
    @RequestMapping(value = "/enable", method = RequestMethod.POST)
    public final ResponseEntity<?> createAndEnable(@RequestBody CREATE_DTO dto) {
        if (!canCreate()) {
            return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
        }
        if (!canEnable()) {
            return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
        }

        IdDto domainId = getEditableService().create(getUserLogin(), dto);
        getEditableService().enable(getUserLogin(), domainId.getId());
        return new Envelope(domainId).toResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/enable", method = RequestMethod.PUT)
    public final ResponseEntity<?> enable(@PathVariable String id, @RequestBody(required=false) CREATE_DTO dto) {
        if (!canEnable()) {
            return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
        }
        if (dto != null) {
            if (!canUpdate()) {
                return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
            }

            getEditableService().update(getUserLogin(), id, dto);
        }

        getEditableService().enable(getUserLogin(), id);
        return new Envelope(Meta.OK).toResponseEntity(HttpStatus.OK);
    }

    // LIST
    protected ResponseEntity<?> _getList(String search, Boolean active, Boolean draft, String distributorId,
            Pageable pageable) {
        ListDto<LIST_SIMPLE_DTO> dtos = getEditableService().getList(getUserLogin(), search, active, draft, distributorId,
                pageable);
        return new Envelope(dtos).toResponseEntity(HttpStatus.OK);
    }

    // DETAIL
    protected ResponseEntity<?> _detail(String id) {
        LIST_DETAIL_DTO dto = getEditableService().getById(getUserLogin(), id);
        return new Envelope(dto).toResponseEntity(HttpStatus.OK);
    }

    // NEW
    protected boolean canCreate() {
        return true;
    }

    protected ResponseEntity<?> _create(CREATE_DTO dto) {
        if (!canCreate()) {
            return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
        }

        IdDto domainId = getEditableService().create(getUserLogin(), dto);
        return new Envelope(domainId).toResponseEntity(HttpStatus.OK);
    }

    // UPDATE
    protected boolean canUpdate() {
        return true;
    }

    protected ResponseEntity<?> _update(String id, CREATE_DTO dto) {
        if (!canUpdate()) {
            return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
        }

        IdDto domainId = getEditableService().update(getUserLogin(), id, dto);
        return new Envelope(domainId).toResponseEntity(HttpStatus.OK);
    }

    // DELETE
    protected boolean canDelete() {
        return true;
    }

    protected ResponseEntity<?> _delete(String id) {
        if (!canDelete()) {
            return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
        }

        getEditableService().delete(getUserLogin(), id);
        return new Envelope(Meta.OK).toResponseEntity(HttpStatus.OK);
    }

    // ENABLE
    protected boolean canEnable() {
        return true;
    }

    // SET ACTIVE
    protected boolean canSetActive() {
        return true;
    }

    protected ResponseEntity<?> _setActive(String id, boolean active) {
        if (!canSetActive()) {
            return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
        }

        getEditableService().setActive(getUserLogin(), id, active);
        return new Envelope(Meta.OK).toResponseEntity(HttpStatus.OK);
    }

}
