package com.viettel.backend.service.category.readonly.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.viettel.backend.domain.Distributor;
import com.viettel.backend.dto.common.CategorySimpleDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.CategoryRepository;
import com.viettel.backend.repository.DistributorRepository;
import com.viettel.backend.service.category.readonly.DistributorService;

@Service
public class DistributorServiceImpl extends AbstractCategoryReadonlyService<Distributor, CategorySimpleDto> implements
        DistributorService {

    @Autowired
    private DistributorRepository distributorRepository;

    @Override
    public CategorySimpleDto createSimpleDto(UserLogin userLogin, Distributor domain) {
        return new CategorySimpleDto(domain);
    }
    
    @Override
    public ListDto<CategorySimpleDto> getAll(UserLogin userLogin, String _distributorId) {
        List<Distributor> domains = getAccessibleDistributors(userLogin);
        if (CollectionUtils.isEmpty(domains)) {
            return ListDto.emptyList();
        }

        List<CategorySimpleDto> dtos = new ArrayList<CategorySimpleDto>(domains.size());
        for (Distributor domain : domains) {
            dtos.add(createSimpleDto(userLogin, domain));
        }

        return new ListDto<CategorySimpleDto>(dtos);
    }

    @Override
    public CategoryRepository<Distributor> getRepository() {
        return distributorRepository;
    }

}
