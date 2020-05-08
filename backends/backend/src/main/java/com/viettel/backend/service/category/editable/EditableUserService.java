package com.viettel.backend.service.category.editable;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.viettel.backend.dto.category.UserCreateDto;
import com.viettel.backend.dto.category.UserDto;
import com.viettel.backend.dto.common.IdDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.common.CategorySeletionDto;
import com.viettel.backend.oauth2.core.UserLogin;

public interface EditableUserService {

    /**
     * Get list of POs with <code>draft=any</code> and <code>active=any</code>
     */
    public ListDto<UserDto> getList(UserLogin userLogin, String search, Boolean active, Boolean draft, String role,
            String distributorId, Pageable pageable);

    /** Get PO detail with <code>draft=any</code> and <code>active=any</code> */
    public UserDto getById(UserLogin userLogin, String id);

    /**
     * Create Draft PO with <code>draft=true</code> and <code>active=true</code>
     */
    public IdDto create(UserLogin userLogin, UserCreateDto dto);

    /** Update PO with <code>draft=any</code> and <code>active=true</code> */
    public IdDto update(UserLogin userLogin, String id, UserCreateDto dto);

    /** Enable Draft PO */
    public boolean enable(UserLogin userLogin, String id);

    /** Delete Draft PO */
    public boolean delete(UserLogin userLogin, String id);

    /**
     * Set PO <code>active</code> status, only affected PO with
     * <code>draft=false</code>
     */
    public void setActive(UserLogin userLogin, String id, boolean active);

    public void resetPassword(UserLogin userLogin, String id);

    public ListDto<CategorySeletionDto> getDistributorOfObserver(UserLogin userLogin, String id);

    public void updateDistributorSetForObserver(UserLogin userLogin, String id, List<String> distributorIds);

}
