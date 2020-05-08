package com.viettel.backend.service.category.readonly.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.util.CollectionUtils;

import com.viettel.backend.domain.Category;
import com.viettel.backend.domain.Distributor;
import com.viettel.backend.dto.common.CategorySimpleDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.service.category.AbstractCategoryService;
import com.viettel.backend.service.category.readonly.I_ReadonlyCategoryService;

public abstract class AbstractCategoryReadonlyService<DOMAIN extends Category, SIMPLE_DTO extends CategorySimpleDto>
        extends AbstractCategoryService<DOMAIN> implements I_ReadonlyCategoryService<SIMPLE_DTO> {

    public abstract SIMPLE_DTO createSimpleDto(UserLogin userLogin, DOMAIN domain);
    
    private Class<SIMPLE_DTO> simpleDtoClass;
    
    @SuppressWarnings("unchecked")
    public AbstractCategoryReadonlyService() {
        Class<?> superClazz = getClass();
        Type superType = superClazz.getGenericSuperclass();
        while (!(superType instanceof ParameterizedType)) {
            superClazz = superClazz.getSuperclass();
            superType = superClazz.getGenericSuperclass();
        }

        int paraIndex = 0;
        ParameterizedType genericSuperclass = (ParameterizedType) superType;
        this.simpleDtoClass = (Class<SIMPLE_DTO>) genericSuperclass.getActualTypeArguments()[paraIndex++];
    }

    @Override
    public ListDto<SIMPLE_DTO> getAll(UserLogin userLogin, String _distributorId) {
        ObjectId distributorId = null;
        if (isUseDistributor()) {
            if (_distributorId == null) {
                Distributor defaultDistributor = getDefaultDistributor(userLogin);
                BusinessAssert.notNull(defaultDistributor, "DistributorID is required");

                distributorId = defaultDistributor.getId();
            } else {
                distributorId = getObjectId(_distributorId);
                Set<ObjectId> accessibleDistributorIds = getIdSet(getAccessibleDistributors(userLogin));

                BusinessAssert.contain(accessibleDistributorIds, distributorId, "Invalid Distributor with ID="
                        + _distributorId);
            }
        }

        List<DOMAIN> domains = getRepository().getAll(userLogin.getClientId(), distributorId);
        if (CollectionUtils.isEmpty(domains)) {
            return ListDto.emptyList();
        }

        List<SIMPLE_DTO> dtos = new ArrayList<SIMPLE_DTO>(domains.size());
        for (DOMAIN domain : domains) {
            dtos.add(createSimpleDto(userLogin, domain));
        }

        return new ListDto<SIMPLE_DTO>(dtos, Long.valueOf(dtos.size()));
    }
    
    @Override
    public Class<SIMPLE_DTO> getSimpleDtoClass() {
        return simpleDtoClass;
    }

}
