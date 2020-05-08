package com.viettel.backend.service.category.readonly.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viettel.backend.domain.UOM;
import com.viettel.backend.dto.common.CategorySimpleDto;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.CategoryRepository;
import com.viettel.backend.repository.UOMRepository;
import com.viettel.backend.service.category.readonly.UOMService;

@Service
public class UOMServiceImpl extends AbstractCategoryReadonlyService<UOM, CategorySimpleDto> implements
        UOMService {

    @Autowired
    private UOMRepository uomRepository;

    @Override
    public CategorySimpleDto createSimpleDto(UserLogin userLogin, UOM domain) {
        return new CategorySimpleDto(domain);
    }

    @Override
    public CategoryRepository<UOM> getRepository() {
        return uomRepository;
    }

}
