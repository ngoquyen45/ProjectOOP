package com.viettel.backend.repository.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.viettel.backend.domain.Feedback;
import com.viettel.backend.domain.Visit;
import com.viettel.backend.repository.FeedbackRepository;
import com.viettel.backend.util.CriteriaUtils;

@Repository
public class FeedbackRepositoryImpl extends AbstractRepository<Visit> implements FeedbackRepository {

    @Override
    protected Criteria getDefaultCriteria() {
        Criteria isVisitCriteria = Criteria.where(Visit.COLUMNNAME_IS_VISIT).is(true);
        Criteria visitStatusCriteria = Criteria.where(Visit.COLUMNNAME_VISIT_STATUS).is(Visit.VISIT_STATUS_VISITED);
        Criteria hasFeedbackCriteria = Criteria.where(Visit.COLUMNNAME_FEEDBACKS).ne(null);

        return CriteriaUtils.andOperator(isVisitCriteria, visitStatusCriteria, hasFeedbackCriteria);
    }

    @Override
    public Feedback getById(ObjectId clientId, ObjectId visitId) {
        return _getById(clientId, false, true, visitId);
    }
    
    @Override
    public List<Feedback> getFeedbackByDistributors(ObjectId clientId, Collection<ObjectId> distributorIds,
            Boolean readStatus, Pageable pageable, Sort sort) {
        Criteria distributorCriteria = Criteria.where(Visit.COLUMNNAME_DISTRIBUTOR_ID).in(distributorIds);
        Criteria criteria = distributorCriteria;
        if (readStatus != null) {
            Criteria readStatusCriteria = Criteria.where(Visit.COLUMNNAME_IS_FEEDBACKS_READED).is(readStatus);
            criteria = CriteriaUtils.andOperator(distributorCriteria, readStatusCriteria);
        }
        
        List<Visit> visits = super._getList(clientId, false, true, criteria, pageable, sort);

        if (visits == null || visits.isEmpty()) {
            return Collections.emptyList();
        }
        
        return new ArrayList<Feedback>(visits);
    }
    
    @Override
    public long countFeedbackByDistributors(ObjectId clientId, Collection<ObjectId> distributorIds, Boolean readStatus) {
        Criteria distributorCriteria = Criteria.where(Visit.COLUMNNAME_DISTRIBUTOR_ID).in(distributorIds);
        Criteria criteria = distributorCriteria;
        if (readStatus != null) {
            Criteria readStatusCriteria = Criteria.where(Visit.COLUMNNAME_IS_FEEDBACKS_READED).is(readStatus);
            criteria = CriteriaUtils.andOperator(distributorCriteria, readStatusCriteria);
        }
        
        return _count(clientId, false, true, criteria);
    }

    @Override
    public List<Feedback> getFeedbackByCustomers(ObjectId clientId, Collection<ObjectId> customerIds, Pageable pageable,
            Sort sort) {
        Criteria customerCriteria = Criteria.where(Visit.COLUMNNAME_CUSTOMER_ID).in(customerIds);
        List<Visit> visits = super._getList(clientId, false, true, customerCriteria, pageable, sort);

        if (visits == null || visits.isEmpty()) {
            return Collections.emptyList();
        }
        
        return new ArrayList<Feedback>(visits);
    }

    @Override
    public long countFeedbackByDistributorsUnread(ObjectId clientId, Collection<ObjectId> distributorIds) {
        Criteria customerCriteria = Criteria.where(Visit.COLUMNNAME_DISTRIBUTOR_ID).in(distributorIds);
        Criteria unreadCriteria = Criteria.where(Visit.COLUMNNAME_IS_FEEDBACKS_READED).is(false);

        return _count(clientId, false, true, CriteriaUtils.andOperator(customerCriteria, unreadCriteria));
    }

    @Override
    public void markAsRead(ObjectId clientId, ObjectId visitId) {
        List<ObjectId> ids = new ArrayList<ObjectId>();
        ids.add(visitId);

        Update update = new Update();
        update.set(Visit.COLUMNNAME_IS_FEEDBACKS_READED, true);

        super._updateMulti(clientId, false, true, ids, update);
    }

}
