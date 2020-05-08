package com.viettel.backend.dto.visit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.viettel.backend.dto.order.OrderCreateDto;

public class VisitEndDto implements Serializable {

    private static final long serialVersionUID = 865188777179374746L;

    private String photo;
    private OrderCreateDto order;
    private List<String> feedbacks;
    private List<SurveyAnswerDto> surveyAnswers;

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public OrderCreateDto getOrder() {
        return order;
    }

    public void setOrder(OrderCreateDto order) {
        this.order = order;
    }

    public List<String> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<String> feedbacks) {
        this.feedbacks = feedbacks;
    }

    public List<SurveyAnswerDto> getSurveyAnswers() {
        return surveyAnswers;
    }

    public void setSurveyAnswers(List<SurveyAnswerDto> surveyAnswers) {
        this.surveyAnswers = surveyAnswers;
    }

    public void addSurveyAnswers(SurveyAnswerDto surveyAnswer) {
        if (this.surveyAnswers == null) {
            this.surveyAnswers = new ArrayList<SurveyAnswerDto>();
        }

        this.surveyAnswers.add(surveyAnswer);
    }

}
