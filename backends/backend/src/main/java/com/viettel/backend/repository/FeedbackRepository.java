package com.viettel.backend.repository;

import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.viettel.backend.domain.Feedback;

public interface FeedbackRepository {

    public List<Feedback> getFeedbackByDistributors(ObjectId clientId, Collection<ObjectId> distributorIds,
            Boolean readStatus, Pageable pageable, Sort sort);

    public long countFeedbackByDistributors(ObjectId clientId, Collection<ObjectId> distributorIds,  Boolean readStatus);
    
    public List<Feedback> getFeedbackByCustomers(ObjectId clientId, Collection<ObjectId> customerIds,
            Pageable pageable, Sort sort);

    public Feedback getById(ObjectId clientId, ObjectId feedbackId);

    public long countFeedbackByDistributorsUnread(ObjectId clientId, Collection<ObjectId> distributorIds);

    public void markAsRead(ObjectId clientId, ObjectId feedbackId);

}
