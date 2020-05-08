package com.viettel.backend.service.category.readonly.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viettel.backend.domain.Route;
import com.viettel.backend.dto.common.CategorySimpleDto;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.CategoryRepository;
import com.viettel.backend.repository.RouteRepository;
import com.viettel.backend.service.category.readonly.RouteService;

@Service
public class RouteServiceImpl extends AbstractCategoryReadonlyService<Route, CategorySimpleDto> implements
        RouteService {

    @Autowired
    private RouteRepository routeRepository;

    @Override
    public CategorySimpleDto createSimpleDto(UserLogin userLogin, Route domain) {
        return new CategorySimpleDto(domain);
    }

    @Override
    public CategoryRepository<Route> getRepository() {
        return routeRepository;
    }

}
