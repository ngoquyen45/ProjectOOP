package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by Thanh on 3/26/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SurveyAnswerDto implements Parcelable, Serializable {

    public static final Creator<SurveyAnswerDto> CREATOR = new Creator<SurveyAnswerDto>() {
        public SurveyAnswerDto createFromParcel(Parcel in) {
            return new SurveyAnswerDto(in);
        }

        public SurveyAnswerDto[] newArray(int size) {
            return new SurveyAnswerDto[size];
        }
    };

    private String surveyId;
    private String[] options;

    public SurveyAnswerDto() {

    }

    public SurveyAnswerDto(Parcel in) {
        surveyId = in.readString();
        options = in.createStringArray();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(surveyId);
        dest.writeStringArray(options);
    }

    public String getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(String surveyId) {
        this.surveyId = surveyId;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }
}
