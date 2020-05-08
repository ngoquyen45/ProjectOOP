package com.viettel.backend.repository.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.util.Assert;

import com.viettel.backend.domain.Category;
import com.viettel.backend.domain.PO;
import com.viettel.backend.repository.CategoryRepository;
import com.viettel.backend.util.CriteriaUtils;

public class CategoryRepositoryImpl<D extends Category> extends BasicRepositoryImpl<D> implements CategoryRepository<D> {

    // DRAFT = FALSE & ACTIVE = TRUE
    @Override
    public List<D> getAll(ObjectId clientId, ObjectId distributorId) {
        Criteria distributorCriteria = null;
        if (Category.isUseDistributor(getDomainClazz()) && distributorId != null) {
            distributorCriteria = Criteria.where(Category.COLUMNNAME_DISTRIBUTOR_ID).is(distributorId);
        }
        
        return _getList(clientId, false, true, distributorCriteria, null, null);
    }

    // ALL
    @Override
    public List<D> getList(ObjectId clientId, Boolean draft, Boolean active, Collection<ObjectId> distributorIds,
            String search, Pageable pageable, Sort sort) {
        Criteria distributorCriteria = null;
        if (Category.isUseDistributor(getDomainClazz()) && distributorIds != null) {
            if (distributorIds.isEmpty()) {
                return Collections.emptyList();
            }
            
            distributorCriteria = Criteria.where(Category.COLUMNNAME_DISTRIBUTOR_ID).in(distributorIds);
        }
        
        Criteria searchCriteria = CriteriaUtils.getSearchLikeCriteria(Category.COLUMNNAME_SEARCH, search);
        
        Criteria criteria = CriteriaUtils.andOperator(distributorCriteria, searchCriteria);
        
        return _getList(clientId, draft, active, criteria, pageable, sort);
    }

    @Override
    public long count(ObjectId clientId, Boolean draft, Boolean active, Collection<ObjectId> distributorIds,
            String search) {
        Criteria distributorCriteria = null;
        if (Category.isUseDistributor(getDomainClazz()) && distributorIds != null) {
            if (distributorIds.isEmpty()) {
                return 0;
            }
            
            distributorCriteria = Criteria.where(Category.COLUMNNAME_DISTRIBUTOR_ID).in(distributorIds);
        }
        
        Criteria searchCriteria = CriteriaUtils.getSearchLikeCriteria(Category.COLUMNNAME_SEARCH, search);
        
        Criteria criteria = CriteriaUtils.andOperator(distributorCriteria, searchCriteria);
        
        return _count(clientId, draft, active, criteria);
    }

    @Override
    public boolean checkNameExist(ObjectId clientId, ObjectId id, String name, ObjectId distributorId) {
        Criteria notMe = null;
        if (id != null) {
            notMe = Criteria.where(PO.COLUMNNAME_ID).ne(id);
        }
        
        Assert.notNull(name);
        Criteria nameCriteria = CriteriaUtils.getSearchInsensitiveCriteria(Category.COLUMNNAME_NAME, name);
        
        Criteria distributorCriteria = null;
        if (Category.isUseDistributor(getDomainClazz())) {
            Assert.notNull(distributorId);
            distributorCriteria = Criteria.where(Category.COLUMNNAME_DISTRIBUTOR_ID).is(distributorId);
        }
        
        Criteria criteria = CriteriaUtils.andOperator(notMe, nameCriteria, distributorCriteria);
        
        return super._exists(clientId, null, null, criteria);
    }
    
    @Override
    public boolean checkCodeExist(ObjectId clientId, ObjectId id, String code, ObjectId distributorId) {
        Criteria notMe = null;
        if (id != null) {
            notMe = Criteria.where(PO.COLUMNNAME_ID).ne(id);
        }
        Criteria codeCriteria = null;
        if (Category.isUseCode(getDomainClazz())) {
            Assert.notNull(code);
            codeCriteria = CriteriaUtils.getSearchInsensitiveCriteria(Category.COLUMNNAME_CODE, code);
        }
        
        Criteria distributorCriteria = null;
        if (Category.isUseDistributor(getDomainClazz())) {
            Assert.notNull(distributorId);
            distributorCriteria = Criteria.where(Category.COLUMNNAME_DISTRIBUTOR_ID).is(distributorId);
        }
        
        Criteria criteria = CriteriaUtils.andOperator(notMe, codeCriteria, distributorCriteria);
        
        return super._exists(clientId, null, null, criteria);
    }

    @Override
    public boolean checkDistributorUsed(ObjectId clientId, ObjectId distributorId) {
        Assert.notNull(distributorId);

        Criteria criteria = Criteria.where(Category.COLUMNNAME_DISTRIBUTOR_ID).is(distributorId);

        return _checkUsed(clientId, criteria);
    }
    
    // PROTECTED
    protected boolean _checkUsed(ObjectId clientId, Criteria criteria) {
        return super._exists(clientId, true, null, criteria) || super._exists(clientId, false, true, criteria);
    }
    
}
