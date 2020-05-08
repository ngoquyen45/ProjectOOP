package com.viettel.backend.service.category.editable;

import org.springframework.data.domain.Pageable;

import com.viettel.backend.dto.common.CategoryCreateDto;
import com.viettel.backend.dto.common.CategoryDto;
import com.viettel.backend.dto.common.IdDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.oauth2.core.UserLogin;

public interface I_EditableCategoryService<LIST_SIMPLE_DTO extends CategoryDto, 
    LIST_DETAIL_DTO extends LIST_SIMPLE_DTO, CREATE_DTO extends CategoryCreateDto> {

    /** Get list of POs with <code>draft=any</code> and <code>active=any</code> */
    public ListDto<LIST_SIMPLE_DTO> getList(UserLogin userLogin, String search, Boolean active, Boolean draft, String distributorId, Pageable pageable);

    /** Get PO detail with <code>draft=any</code> and <code>active=any</code> */
    public LIST_DETAIL_DTO getById(UserLogin userLogin, String id);

    /** Create Draft PO with <code>draft=true</code> and <code>active=true</code> */
    public IdDto create(UserLogin userLogin, CREATE_DTO dto);

    /** Update PO with <code>draft=any</code> and <code>active=true</code> */
    public IdDto update(UserLogin userLogin, String id, CREATE_DTO dto);

    /** Enable Draft PO */
    public boolean enable(UserLogin userLogin, String id);

    /** Delete Draft PO */
    public boolean delete(UserLogin userLogin, String id);
    
    /** Set PO <code>active</code> status, only affected PO with <code>draft=false</code> */
    public void setActive(UserLogin userLogin, String id, boolean active);
    
}
