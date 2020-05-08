package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Thanh
 * @since 3/11/2015
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EndVisitingRequest implements Parcelable {

    public static final Creator<EndVisitingRequest> CREATOR = new Creator<EndVisitingRequest>() {
        public EndVisitingRequest createFromParcel(Parcel in) {
            return new EndVisitingRequest(in);
        }

        public EndVisitingRequest[] newArray(int size) {
            return new EndVisitingRequest[size];
        }
    };

    private PlaceOrderRequest order;
    private String[] feedbacks;
    private ExhibitionRatingDto[] exhibitionRatings;
    private SurveyAnswerDto[] surveyAnswers;
    private String photo;

    public EndVisitingRequest() {

    }

    public EndVisitingRequest(Parcel in) {
        ClassLoader classLoader = getClass().getClassLoader();
        order = in.readParcelable(classLoader);
        in.readStringArray(feedbacks);
        surveyAnswers = in.createTypedArray(SurveyAnswerDto.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(order, flags);
        dest.writeStringArray(feedbacks);
        dest.writeTypedArray(surveyAnswers, flags);
    }
        //region get/set
    public ExhibitionRatingDto[] getExhibitionRatings() {
        return exhibitionRatings;
    }

    public void setExhibitionRatings(ExhibitionRatingDto[] exhibitionRatings) {
        this.exhibitionRatings = exhibitionRatings;
    }

    public PlaceOrderRequest getOrder() {
        return order;
    }

    public void setOrder(PlaceOrderRequest order) {
        this.order = order;
    }

    public SurveyAnswerDto[] getSurveyAnswers() {
        return surveyAnswers;
    }

    public void setSurveyAnswers(SurveyAnswerDto[] surveyAnswers) {
        this.surveyAnswers = surveyAnswers;
    }

    public String[] getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(String[] feedbacks) {
        this.feedbacks = feedbacks;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

//endregion
}
