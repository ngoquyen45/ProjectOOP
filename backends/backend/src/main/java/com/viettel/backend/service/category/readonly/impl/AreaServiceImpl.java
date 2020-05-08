package com.viettel.backend.service.category.readonly.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viettel.backend.domain.Area;
import com.viettel.backend.dto.common.CategorySimpleDto;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.AreaRepository;
import com.viettel.backend.repository.CategoryRepository;
import com.viettel.backend.service.category.readonly.AreaService;

@Service
public class AreaServiceImpl extends AbstractCategoryReadonlyService<Area, CategorySimpleDto> implements
        AreaService {

    @Autowired
    private AreaRepository areaRepository;

    @Override
    public CategorySimpleDto createSimpleDto(UserLogin userLogin, Area domain) {
        return new CategorySimpleDto(domain);
    }

    @Override
    public CategoryRepository<Area> getRepository() {
        return areaRepository;
    }

}
