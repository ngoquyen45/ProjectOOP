package com.viettel.backend.service.category;

import java.util.Set;

import org.bson.types.ObjectId;

import com.viettel.backend.domain.Category;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.CategoryRepository;
import com.viettel.backend.service.common.AbstractService;

public abstract class AbstractCategoryService<DOMAIN extends Category> extends AbstractService {

    public abstract CategoryRepository<DOMAIN> getRepository();

    protected boolean isUseDistributor() {
        return Category.isUseDistributor(getRepository().getDomainClass());
    }
    
    protected boolean isUseCode() {
        return Category.isUseCode(getRepository().getDomainClass());
    }
    
    protected boolean isAutoGenerateCode() {
        return Category.isAutoGenerateCode(getRepository().getDomainClass());
    }

    protected void checkAccessible(UserLogin userLogin, DOMAIN domain) {
        if (isUseDistributor()) {
            Set<ObjectId> accessibleDistributorIds = getIdSet(getAccessibleDistributors(userLogin));
            BusinessAssert.contain(accessibleDistributorIds, domain.getDistributor().getId(),
                    "Cannot access Distributor with ID=" + domain.getDistributor().getId());
        }
    }
    
}
