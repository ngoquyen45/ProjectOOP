package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.viettel.dmsplus.sdk.network.IsoDateDeserializer;

import java.util.Date;

/**
 * @author Thanh
 * @since 3/24/2015
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerVisitInfo extends OrderDetailResult implements Parcelable {

    public static final Creator<CustomerVisitInfo> CREATOR = new Creator<CustomerVisitInfo>() {
        public CustomerVisitInfo createFromParcel(Parcel in) {
            return new CustomerVisitInfo(in);
        }

        public CustomerVisitInfo[] newArray(int size) {
            return new CustomerVisitInfo[size];
        }
    };

    private boolean hasOrder;

    private boolean closed;

    @JsonDeserialize(using = IsoDateDeserializer.class)
    private Date startTime;

    @JsonDeserialize(using = IsoDateDeserializer.class)
    private Date endTime;

    private String closingPhoto;
    private String photo;

    /* Feedback cua lan ghe tham nay*/
    private String[] feedbacks;

    private SurveyListResult.SurveyDto[] surveys;
    private SurveyAnswerDto[] surveyAnswers;

    private ExhibitionDto[] exhibitions;
    private ExhibitionRatingDto[] exhibitionRatings;

    public CustomerVisitInfo() {
    }

    public CustomerVisitInfo(Parcel in) {
        super(in);
        hasOrder = in.readByte() != 0;
        closed = in.readByte() != 0;
        long time = in.readLong();
        startTime = time != 0 ? new Date(time) : null;
        time = in.readLong();
        endTime = time != 0 ? new Date(time) : null;
        closingPhoto = in.readString();
        feedbacks = in.createStringArray();
        surveys = in.createTypedArray(SurveyListResult.SurveyDto.CREATOR);
        surveyAnswers = in.createTypedArray(SurveyAnswerDto.CREATOR);
        exhibitions = in.createTypedArray(ExhibitionDto.CREATOR);
        exhibitionRatings = in.createTypedArray(ExhibitionRatingDto.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flag) {
        super.writeToParcel(dest, flag);
        dest.writeByte((byte) (hasOrder ? 1 : 0));
        dest.writeByte((byte) (closed ? 1 : 0));
        dest.writeLong(startTime != null ? startTime.getTime() : 0);
        dest.writeLong(endTime != null ? endTime.getTime() : 0);
        dest.writeString(closingPhoto);
        dest.writeStringArray(feedbacks);
        dest.writeTypedArray(surveys, flag);
        dest.writeTypedArray(surveyAnswers, flag);
        dest.writeTypedArray(exhibitions, flag);
        dest.writeTypedArray(exhibitionRatings, flag);
    }
        //region getter/setter
    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getClosingPhoto() {
        return closingPhoto;
    }

    public void setClosingPhoto(String closingPhoto) {
        this.closingPhoto = closingPhoto;
    }

    public String[] getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(String[] feedbacks) {
        this.feedbacks = feedbacks;
    }

    public SurveyAnswerDto[] getSurveyAnswers() {
        return surveyAnswers;
    }

    public void setSurveyAnswers(SurveyAnswerDto[] surveyAnswers) {
        this.surveyAnswers = surveyAnswers;
    }

    public boolean isHasOrder() {
        return hasOrder;
    }

    public void setHasOrder(boolean hasOrder) {
        this.hasOrder = hasOrder;
    }

    public ExhibitionRatingDto[] getExhibitionRatings() {
        return exhibitionRatings;
    }

    public void setExhibitionRatings(ExhibitionRatingDto[] exhibitionRatings) {
        this.exhibitionRatings = exhibitionRatings;
    }

    public SurveyListResult.SurveyDto[] getSurveys() {
        return surveys;
    }

    public void setSurveys(SurveyListResult.SurveyDto[] surveys) {
        this.surveys = surveys;
    }

    public ExhibitionDto[] getExhibitions() {
        return exhibitions;
    }

    public void setExhibitions(ExhibitionDto[] exhibitions) {
        this.exhibitions = exhibitions;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
    //endregion
}
