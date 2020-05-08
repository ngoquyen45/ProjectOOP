package com.viettel.backend.domain;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.viettel.backend.domain.embed.OrderDetail;
import com.viettel.backend.domain.embed.OrderPromotion;
import com.viettel.backend.domain.embed.SurveyAnswer;
import com.viettel.backend.domain.embed.UserEmbed;
import com.viettel.backend.util.entity.SimpleDate;

@Document(collection = "VisitAndOrder")
public class Visit extends Order implements Feedback {

    private static final long serialVersionUID = 7248716614988497636L;

    public static final String COLUMNNAME_IS_VISIT = "isVisit";
    public static final String COLUMNNAME_VISIT_STATUS = "visitStatus";
    public static final String COLUMNNAME_CLOSED = "closed";
    public static final String COLUMNNAME_START_TIME = "startTime";
    public static final String COLUMNNAME_START_TIME_VALUE = "startTime.value";
    public static final String COLUMNNAME_START_TIME_MONTH = "startTime.month";
    public static final String COLUMNNAME_START_TIME_YEAR = "startTime.year";

    public static final String COLUMNNAME_FEEDBACKS = "feedbacks";

    public static final String COLUMNNAME_SURVEY_ANSWERS = "surveyAnswers";
    
    public static final String COLUMNNAME_SALESMAN_ID = Order.COLUMNNAME_CREATED_BY_ID;
    
    public static final int VISIT_STATUS_VISITING = 0;
    public static final int VISIT_STATUS_VISITED = 1;

    public static final int LOCATION_STATUS_LOCATED = 0;
    public static final int LOCATION_STATUS_TOO_FAR = 1;
    public static final int LOCATION_STATUS_UNLOCATED = 2;
    public static final int LOCATION_STATUS_CUSTOMER_UNLOCATED = 3;
    
    public static final String COLUMNNAME_PHOTO = "photo";

    public Visit() {
        super();

        setOrder(false);
        setVisit(true);
    }

    private int visitStatus;
    private boolean closed;
    
    private String photo;

    private double[] location;
    private double[] customerLocation;
    private int locationStatus;
    private Double distance;

    private long duration;
    private boolean errorDuration;

    // FEEDBACK
    private boolean feedbacksReaded;
    private List<String> feedbacks;

    // SURVEY ANSWER
    private List<SurveyAnswer> surveyAnswers;
    
    public int getVisitStatus() {
        return visitStatus;
    }

    public void setVisitStatus(int visitStatus) {
        this.visitStatus = visitStatus;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public double[] getLocation() {
        return location;
    }

    public void setLocation(double[] location) {
        this.location = location;
    }

    public double[] getCustomerLocation() {
        return customerLocation;
    }

    public void setCustomerLocation(double[] customerLocation) {
        this.customerLocation = customerLocation;
    }

    public int getLocationStatus() {
        return locationStatus;
    }

    public void setLocationStatus(int locationStatus) {
        this.locationStatus = locationStatus;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
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

    public List<SurveyAnswer> getSurveyAnswers() {
        return surveyAnswers;
    }

    public void setSurveyAnswers(List<SurveyAnswer> surveyAnswers) {
        this.surveyAnswers = surveyAnswers;
    }

    /**
     * Increase method's visibility to public  
     */
    @Override
    public SimpleDate getStartTime() {
        return super.getStartTime();
    }

    /**
     * Increase method's visibility to public  
     */
    @Override
    public void setStartTime(SimpleDate startTime) {
        super.setStartTime(startTime);
    }

    /**
     * Increase method's visibility to public  
     */
    @Override
    public SimpleDate getEndTime() {
        return super.getEndTime();
    }

    /**
     * Increase method's visibility to public  
     */
    @Override
    public void setEndTime(SimpleDate endTime) {
        super.setEndTime(endTime);
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isErrorDuration() {
        return errorDuration;
    }

    public void setErrorDuration(boolean errorDuration) {
        this.errorDuration = errorDuration;
    }

    @Override
    public void setDetails(List<OrderDetail> details) {
        setOrder(true);
        super.setDetails(details);
    }

    @Override
    public void setPromotionResults(List<OrderPromotion> promotionResults) {
        setOrder(true);
        super.setPromotionResults(promotionResults);
    }

    /**
     * Set Order's creation time
     * @deprecated For Visit, please use {@link #setStartTime(SimpleDate)} and {@link #setEndTime(SimpleDate)}
     */
    @Override
    public void setCreatedTime(SimpleDate createdTime) {
        throw new UnsupportedOperationException("use startTime and endTime");
    }
    
    @Override
    public UserEmbed getSalesman() {
        return getCreatedBy();
    }
    
    public void setSalesman(UserEmbed salesman) {
        setCreatedBy(salesman);
    }

}
