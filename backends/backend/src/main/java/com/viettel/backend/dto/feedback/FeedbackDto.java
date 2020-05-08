package com.viettel.backend.dto.feedback;

import java.util.List;

import com.viettel.backend.domain.Feedback;
import com.viettel.backend.dto.common.DTOSimple;

public class FeedbackDto extends DTOSimple {

    private static final long serialVersionUID = 8310676852814109872L;

    private String salesman;
    private String customer;
    private String createdTime;
    private boolean feedbacksReaded;
    private List<String> feedbacks;

    public FeedbackDto(Feedback feedback) {
        super(feedback.getId().toString());
        
        this.salesman = feedback.getSalesman().getFullname();
        this.customer = feedback.getCustomer().getName();
        this.createdTime = feedback.getCreatedTime().getIsoTime();
        this.feedbacksReaded = feedback.isFeedbacksReaded();
        this.feedbacks = feedback.getFeedbacks();
    }
    
    public String getSalesman() {
        return salesman;
    }

    public void setSalesman(String salesman) {
        this.salesman = salesman;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public boolean isFeedbacksReaded() {
        return feedbacksReaded;
    }

    public void setFeedbacksReaded(boolean feedbacksReaded) {
        this.feedbacksReaded = feedbacksReaded;
    }

    public List<String> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<String> feedbacks) {
        this.feedbacks = feedbacks;
    }

}
