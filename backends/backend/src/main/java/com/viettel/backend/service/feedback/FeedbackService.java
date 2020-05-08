package com.viettel.backend.service.feedback;

import org.springframework.data.domain.Pageable;

import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.feedback.FeedbackDto;
import com.viettel.backend.dto.feedback.FeedbackSimpleDto;
import com.viettel.backend.oauth2.core.UserLogin;

public interface FeedbackService {

    public ListDto<FeedbackSimpleDto> getLast10FeedbacksFromCustomer(UserLogin userLogin, String _customerId);

    public ListDto<FeedbackDto> getFeedbacks(UserLogin userLogin, Pageable pageable, Boolean readStatus);

    public FeedbackDto readFeedback(UserLogin userLogin, String feedbackId);

}
