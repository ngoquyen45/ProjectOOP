package com.viettel.backend.repository.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.Assert;

import com.mongodb.WriteResult;
import com.viettel.backend.domain.Category;
import com.viettel.backend.domain.PO;
import com.viettel.backend.domain.POSearchable;
import com.viettel.backend.domain.annotation.ClientRootInclude;
import com.viettel.backend.util.CriteriaUtils;
import com.viettel.backend.util.StringUtils;

public abstract class AbstractRepository<D extends PO> {

    @Autowired
    private MongoTemplate dataTemplate;

    private Class<D> domainClazz;

    @SuppressWarnings("unchecked")
    public AbstractRepository() {
        Class<?> superClazz = getClass();
        Type superType = superClazz.getGenericSuperclass();
        while (!(superType instanceof ParameterizedType)) {
            superClazz = superClazz.getSuperclass();
            superType = superClazz.getGenericSuperclass();
        }

        int paraIndex = 0;
        ParameterizedType genericSuperclass = (ParameterizedType) superType;
        this.domainClazz = (Class<D>) genericSuperclass.getActualTypeArguments()[paraIndex++];
    }

    public Class<D> getDomainClazz() {
        return domainClazz;
    }

    protected final D _getById(ObjectId clientId, Boolean draft, Boolean active, ObjectId id) {
        Criteria criteria = Criteria.where(PO.COLUMNNAME_ID).is(id);
        Query mainQuery = initQuery(clientId, draft, active, criteria, null, null);
        return dataTemplate.findOne(mainQuery, domainClazz);
    }

    protected final D _getFirst(ObjectId clientId, Boolean draft, Boolean active, Criteria criteria, Sort sort) {
        Query mainQuery = initQuery(clientId, draft, active, criteria, null, sort);
        return dataTemplate.findOne(mainQuery, domainClazz);
    }

    protected final List<D> _getList(ObjectId clientId, Boolean draft, Boolean active, Criteria criteria,
            Pageable pageable, Sort sort) {
        Query mainQuery = initQuery(clientId, draft, active, criteria, pageable, sort);
        return dataTemplate.find(mainQuery, domainClazz);
    }

    protected final Long _count(ObjectId clientId, Boolean draft, Boolean active, Criteria criteria) {
        Query mainQuery = initQuery(clientId, draft, active, criteria, null, null);
        return dataTemplate.count(mainQuery, domainClazz);
    }

    protected final boolean _exists(ObjectId clientId, Boolean draft, Boolean active, ObjectId id) {
        Criteria criteria = Criteria.where(PO.COLUMNNAME_ID).is(id);
        return this._exists(clientId, draft, active, criteria);
    }

    protected final boolean _exists(ObjectId clientId, Boolean draft, Boolean active, Criteria criteria) {
        Query mainQuery = initQuery(clientId, draft, active, criteria, null, null);
        return dataTemplate.exists(mainQuery, domainClazz);
    }

    protected final D _save(ObjectId clientId, D domain) {
        Assert.notNull(domain);
        Assert.notNull(domain.getClientId());
        Assert.notNull(clientId);
        if (!domain.getClientId().equals(clientId)) {
            throw new IllegalArgumentException("client id not valid");
        }

        if (domain instanceof POSearchable) {
            POSearchable searchableDomain = (POSearchable) domain;
            String searchValue = "";

            String[] sources = searchableDomain.getSearchValues();
            if (sources != null && sources.length > 0) {
                String value = StringUtils.arrayToDelimitedString(sources, " ");
                searchValue = StringUtils.getSearchableString(value);
            }

            searchableDomain.setSearch(searchValue);
        }

        dataTemplate.save(domain);

        onChange(clientId);

        return domain;
    }

    protected final boolean _delete(ObjectId clientId, ObjectId id) {
        Assert.notNull(id);

        Criteria criteria = Criteria.where(PO.COLUMNNAME_ID).is(id);
        Query query = initQuery(clientId, true, null, criteria, null, null);
        WriteResult result = dataTemplate.remove(query, domainClazz);

        onChange(clientId);

        return result.getN() == 1;
    }

    protected final boolean _delete(ObjectId clientId, Collection<ObjectId> ids) {
        Assert.notEmpty(ids);

        Criteria criteria = Criteria.where(PO.COLUMNNAME_ID).in(ids);
        Query query = initQuery(clientId, true, null, criteria, null, null);
        WriteResult result = dataTemplate.remove(query, domainClazz);

        onChange(clientId);

        return result.getN() == ids.size();
    }

    protected final void _insertBatch(ObjectId clientId, Collection<D> domains) {
        Assert.notEmpty(domains);

        // CHECK
        for (D domain : domains) {
            Assert.notNull(domain);
            Assert.notNull(domain.getClientId());
            Assert.notNull(clientId);
            if (!domain.getClientId().equals(clientId)) {
                throw new IllegalArgumentException("client id not valid");
            }

            if (domain instanceof POSearchable) {
                POSearchable searchableDomain = (POSearchable) domain;
                String searchValue = "";

                String[] sources = searchableDomain.getSearchValues();
                if (sources != null && sources.length > 0) {
                    String value = StringUtils.arrayToDelimitedString(sources, " ");
                    searchValue = StringUtils.getSearchableString(value);
                }

                searchableDomain.setSearch(searchValue);
            }
        }

        onChange(clientId);

        dataTemplate.insert(domains, domainClazz);
    }

    protected final int _updateMulti(ObjectId clientId, Boolean draft, Boolean active, Collection<ObjectId> ids,
            Update update) {
        Assert.notEmpty(ids);
        Criteria criteria = Criteria.where(PO.COLUMNNAME_ID).in(ids);
        onChange(clientId);
        return this._updateMulti(clientId, draft, active, criteria, update);
    }

    protected final int _updateMulti(ObjectId clientId, Boolean draft, Boolean active, Criteria criteria,
            Update update) {
        Query mainQuery = initQuery(clientId, draft, active, criteria, null, null);
        WriteResult result = dataTemplate.updateMulti(mainQuery, update, domainClazz);
        onChange(clientId);
        return result.getN();
    }

    protected final Map<ObjectId, D> _getMap(List<D> pos) {
        if (pos == null || pos.isEmpty()) {
            return Collections.<ObjectId, D> emptyMap();
        }

        Map<ObjectId, D> results = new HashMap<ObjectId, D>();
        for (D po : pos) {
            results.put(po.getId(), po);
        }

        return results;
    }

    protected Criteria getDefaultCriteria() {
        return null;
    }

    protected void onChange(ObjectId clientId) {
        // DO NOTHING
    }

    /************************************************************************/

    /**************************** PRIVATE METHODS ***************************/
    /************************************************************************/
    final protected Query initQuery(ObjectId clientId, Boolean draft, Boolean active, Criteria criteria, Pageable pageable,
            Sort sort) {
        Assert.notNull(clientId);

        Criteria clientFilterCriteria = null;
        if (isIncludeClientRoot()) {
            clientFilterCriteria = CriteriaUtils.orOperator(Criteria.where(PO.COLUMNNAME_CLIENT_ID).is(clientId),
                    Criteria.where(PO.COLUMNNAME_CLIENT_ID).is(PO.CLIENT_ROOT_ID));
        } else {
            clientFilterCriteria = Criteria.where(PO.COLUMNNAME_CLIENT_ID).is(clientId);
        }

        Criteria activeCriteria = null;
        if (active != null) {
            activeCriteria = Criteria.where(PO.COLUMNNAME_ACTIVE).is(active);
        }

        Criteria draftCriteria = null;
        if (draft != null) {
            draftCriteria = Criteria.where(PO.COLUMNNAME_DRAFT).is(draft);
        }

        Criteria mainCriteria = CriteriaUtils.andOperator(clientFilterCriteria, activeCriteria, draftCriteria,
                getDefaultCriteria(), criteria);

        Query mainQuery = new Query();
        mainQuery.addCriteria(mainCriteria);

        if (sort == null) {
            if (Category.class.isAssignableFrom(domainClazz)) {
                if (Category.isUseDistributor(domainClazz)) {
                    sort = new Sort(new Order(Direction.DESC, PO.COLUMNNAME_DRAFT),
                            new Order(Category.COLUMNNAME_DISTRIBUTOR_NAME), new Order(Category.COLUMNNAME_NAME));
                } else {
                    sort = new Sort(new Order(Direction.DESC, PO.COLUMNNAME_DRAFT),
                            new Order(Category.COLUMNNAME_NAME));
                }

            }
        }

        if (pageable != null) {
            if (sort != null) {
                pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
            }
            mainQuery.with(pageable);
        } else if (sort != null) {
            mainQuery.with(sort);
        }

        return mainQuery;
    }

    private boolean isIncludeClientRoot() {
        return (domainClazz.isAnnotationPresent(ClientRootInclude.class));
    }

    public Class<? extends PO> getDomainClass() {
        return domainClazz;
    }

}
