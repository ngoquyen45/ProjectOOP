package com.viettel.dmsplus.sdk.models;

import java.io.Serializable;

/**
 * Created by PHAMHUNG on 8/29/2015.
 */
public class VisitHolder implements Serializable {
    CustomerForVisit customerInfo;
    OrderHolder orderHolder;
    String visitId;
    CustomerFeedbackModel[] feedback;
    SurveyAnswerDto[] surveyAnswers;
    ExhibitionRatingDto[] rateSubmit;
    String photoPath;
    boolean vanSale;

    public VisitHolder(CustomerForVisit customerInfo, String visitId, OrderHolder orderHolder, CustomerFeedbackModel[] fb, SurveyAnswerDto[] sa, ExhibitionRatingDto[] rs,String photo,boolean vanSale) {
        this.customerInfo = customerInfo;
        this.orderHolder = orderHolder;
        this.visitId = visitId;
        this.feedback = fb;
        this.surveyAnswers = sa;
        this.rateSubmit = rs;
        this.photoPath = photo;
        this.vanSale = vanSale;
    }
        //region get/set
    public CustomerForVisit getCustomerInfo() {
        return customerInfo;
    }

    public void setCustomerInfo(CustomerForVisit customerInfo) {
        this.customerInfo = customerInfo;
    }

    public OrderHolder getOrderHolder() {
        return orderHolder;
    }

    public void setOrderHolder(OrderHolder orderHolder) {
        this.orderHolder = orderHolder;
    }

    public String getVisitId() {
        return visitId;
    }

    public void setVisitId(String visitId) {
        this.visitId = visitId;
    }

    public CustomerFeedbackModel[] getFeedback() {
        return feedback;
    }

    public void setFeedback(CustomerFeedbackModel[] feedback) {
        this.feedback = feedback;
    }

    public SurveyAnswerDto[] getSurveyAnswers() {
        return surveyAnswers;
    }

    public void setSurveyAnswers(SurveyAnswerDto[] surveyAnswers) {
        this.surveyAnswers = surveyAnswers;
    }

    public ExhibitionRatingDto[] getRateSubmit() {
        return rateSubmit;
    }

    public void setRateSubmit(ExhibitionRatingDto[] rateSubmit) {
        this.rateSubmit = rateSubmit;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public boolean isVanSale() {
        return vanSale;
    }

    public void setVanSale(boolean vanSale) {
        this.vanSale = vanSale;
    }
    //endregion
}
