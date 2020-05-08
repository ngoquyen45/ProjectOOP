package com.viettel.backend.service.feedback.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.viettel.backend.domain.Customer;
import com.viettel.backend.domain.Distributor;
import com.viettel.backend.domain.Feedback;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.feedback.FeedbackDto;
import com.viettel.backend.dto.feedback.FeedbackSimpleDto;
import com.viettel.backend.engine.notification.WebNotificationEngine;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.CustomerRepository;
import com.viettel.backend.repository.FeedbackRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.common.AbstractService;
import com.viettel.backend.service.feedback.FeedbackService;

@Service
public class FeedbackServiceImpl extends AbstractService implements FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private WebNotificationEngine webNotificationEngine;

    @RolePermission(value = { Role.SALESMAN })
    @Override
    public ListDto<FeedbackSimpleDto> getLast10FeedbacksFromCustomer(UserLogin userLogin, String _customerId) {
        Customer customer = getMandatoryPO(userLogin, _customerId, customerRepository);
        checkAccessible(userLogin, null, customer);

        Sort sort = new Sort(Direction.DESC, Feedback.COLUMNNAME_CREATED_TIME_VALUE);
        Pageable pageable = new PageRequest(0, 10);
        List<Feedback> feedbacks = feedbackRepository.getFeedbackByCustomers(userLogin.getClientId(),
                Collections.singleton(customer.getId()), pageable, sort);

        List<FeedbackSimpleDto> messages = new ArrayList<>();
        for (Feedback feedback : feedbacks) {
            if (feedback.getFeedbacks() != null) {
                String createdTime = feedback.getCreatedTime().getIsoTime();
                for (String message : feedback.getFeedbacks()) {
                    FeedbackSimpleDto dto = new FeedbackSimpleDto(createdTime, message);
                    messages.add(dto);
                }
            }
        }

        return new ListDto<FeedbackSimpleDto>(messages);
    }

    @RolePermission(value = { Role.ADMIN, Role.OBSERVER, Role.SUPERVISOR, Role.DISTRIBUTOR })
    @Override
    public ListDto<FeedbackDto> getFeedbacks(UserLogin userLogin, Pageable pageable, Boolean readStatus) {
        List<Distributor> distributors = getAccessibleDistributors(userLogin);
        Set<ObjectId> distributorIds = getIdSet(distributors);

        if (distributorIds == null || distributorIds.isEmpty()) {
            return ListDto.emptyList();
        }

        Sort sort = new Sort(new Order(Direction.ASC, Feedback.COLUMNNAME_IS_FEEDBACKS_READED),
                new Order(Direction.DESC, Feedback.COLUMNNAME_CREATED_TIME_VALUE));
        
        List<Feedback> feedbacks = feedbackRepository.getFeedbackByDistributors(userLogin.getClientId(), distributorIds,
                readStatus, pageable, sort);
        if (CollectionUtils.isEmpty(feedbacks) && pageable.getPageNumber() == 0) {
            return ListDto.emptyList();
        }

        List<FeedbackDto> dtos = new ArrayList<FeedbackDto>(feedbacks.size());
        for (Feedback feedback : feedbacks) {
            FeedbackDto dto = new FeedbackDto(feedback);
            dtos.add(dto);
        }

        long count = Long.valueOf(dtos.size());
        if (pageable != null) {
            if (pageable.getPageNumber() > 0 || count == pageable.getPageSize()) {
                count = feedbackRepository.countFeedbackByDistributors(userLogin.getClientId(), distributorIds, readStatus);
            }
        }

        return new ListDto<FeedbackDto>(dtos, count);
    }

    @RolePermission(value = { Role.ADMIN, Role.OBSERVER, Role.SUPERVISOR, Role.DISTRIBUTOR })
    @Override
    public FeedbackDto readFeedback(UserLogin userLogin, String _feedbackId) {
        ObjectId feedbackId = getObjectId(_feedbackId);
        Feedback feedback = feedbackRepository.getById(userLogin.getClientId(), feedbackId);
        BusinessAssert.notNull(feedback, String.format("Cannot find feedback id: %s", _feedbackId));

        BusinessAssert.isTrue(checkAccessible(userLogin, feedback.getDistributor().getId()), "distributor accessible failed");

        if (!feedback.isFeedbacksReaded()) {
            feedbackRepository.markAsRead(userLogin.getClientId(), feedbackId);
            webNotificationEngine.notifyChangedFeedback(userLogin, feedback, WebNotificationEngine.ACTION_FEEDBACK_READ);
        }

        FeedbackDto dto = new FeedbackDto(feedback);
        return dto;
    }

}
