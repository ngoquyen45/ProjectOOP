package com.viettel.backend.dto.visit;

import java.util.LinkedList;
import java.util.List;

import com.viettel.backend.domain.Visit;
import com.viettel.backend.domain.embed.SurveyAnswer;
import com.viettel.backend.dto.category.SurveyDto;
import com.viettel.backend.dto.category.UserSimpleDto;
import com.viettel.backend.dto.common.I_ProductPhotoFactory;
import com.viettel.backend.dto.order.OrderDto;
import com.viettel.backend.util.entity.Location;

public class VisitInfoDto extends OrderDto {

    private static final long serialVersionUID = -4244621688895579808L;

    private boolean hasOrder;

    private int visitStatus;
    private boolean closed;
    private String photo;

    private String startTime;
    private String endTime;

    private long duration;
    private boolean errorDuration;

    private int locationStatus;
    private Double distance;

    // FEEDBACK
    private List<String> feedbacks;

    // SURVEY ANSWER
    private List<SurveyDto> surveys;
    private List<SurveyAnswerDto> surveyAnswers;

    private Location location;
    private Location customerLocation;
    
    /** after this need set surveys and exhibition use in this visit */
    public VisitInfoDto(Visit visit, I_ProductPhotoFactory productPhotoFactory) {
        super(visit, productPhotoFactory);

        this.hasOrder = visit.isOrder();
        this.visitStatus = visit.getVisitStatus();
        this.closed = visit.isClosed();
        this.photo = visit.getPhoto();
        this.locationStatus = visit.getLocationStatus();
        this.distance = visit.getDistance();
        this.feedbacks = visit.getFeedbacks();
        this.startTime = visit.getStartTime() != null ? visit.getStartTime().getIsoTime() : null;
        this.endTime = visit.getEndTime() != null ? visit.getEndTime().getIsoTime() : null;
        this.duration = visit.getDuration();
        this.errorDuration = visit.isErrorDuration();

        if (visit.getLocation() != null && visit.getLocation().length == 2) {
            this.location = new Location(visit.getLocation()[1], visit.getLocation()[0]);
        }

        if (visit.getCustomerLocation() != null && visit.getCustomerLocation().length == 2) {
            this.customerLocation = new Location(visit.getCustomerLocation()[1], visit.getCustomerLocation()[0]);
        }

        if (visit.getSurveyAnswers() != null) {
            for (SurveyAnswer surveyAnswer : visit.getSurveyAnswers()) {
                addSurveyAnswer(new SurveyAnswerDto(surveyAnswer));
            }
        }
    }

    public boolean isHasOrder() {
        return hasOrder;
    }

    public void setHasOrder(boolean hasOrder) {
        this.hasOrder = hasOrder;
    }

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

    public List<String> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<String> feedbacks) {
        this.feedbacks = feedbacks;
    }

    public List<SurveyDto> getSurveys() {
        return surveys;
    }

    public void setSurveys(List<SurveyDto> surveys) {
        this.surveys = surveys;
    }

    public void addSurvey(SurveyDto survey) {
        if (this.surveys == null) {
            this.surveys = new LinkedList<SurveyDto>();
        }

        this.surveys.add(survey);
    }

    public List<SurveyAnswerDto> getSurveyAnswers() {
        return surveyAnswers;
    }

    public void setSurveyAnswers(List<SurveyAnswerDto> surveyAnswers) {
        this.surveyAnswers = surveyAnswers;
    }

    public void addSurveyAnswer(SurveyAnswerDto surveyAnswer) {
        if (this.surveyAnswers == null) {
            this.surveyAnswers = new LinkedList<SurveyAnswerDto>();
        }

        this.surveyAnswers.add(surveyAnswer);
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getCustomerLocation() {
        return customerLocation;
    }

    public void setCustomerLocation(Location customerLocation) {
        this.customerLocation = customerLocation;
    }
    
    public UserSimpleDto getSalesman() {
        return super.getCreatedBy();
    }

}
