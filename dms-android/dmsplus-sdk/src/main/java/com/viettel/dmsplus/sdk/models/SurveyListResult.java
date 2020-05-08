package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.viettel.dmsplus.sdk.network.IsoShortDateDeserializer;

import java.util.Date;

/**
 * @author Thanh
 * @since 3/10/2015
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SurveyListResult implements Parcelable {

    public static final Creator<SurveyListResult> CREATOR = new Creator<SurveyListResult>() {
        public SurveyListResult createFromParcel(Parcel in) {
            return new SurveyListResult(in);
        }

        public SurveyListResult[] newArray(int size) {
            return new SurveyListResult[size];
        }
    };

    private SurveyDto[] list;
    private int count;

    public SurveyListResult() {

    }

    public SurveyListResult(Parcel in) {
        list = in.createTypedArray(SurveyDto.CREATOR);
        count = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(list, flags);
        dest.writeInt(count);
    }

    public SurveyDto[] getList() {
        return list;
    }

    public void setList(SurveyDto[] list) {
        this.list = list;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SurveyDto implements Parcelable {

        public static final Creator<SurveyDto> CREATOR = new Creator<SurveyDto>() {
            public SurveyDto createFromParcel(Parcel in) {
                return new SurveyDto(in);
            }

            public SurveyDto[] newArray(int size) {
                return new SurveyDto[size];
            }
        };

        private String id;
        private String name;
        @JsonDeserialize(using = IsoShortDateDeserializer.class)
        private Date startDate;
        @JsonDeserialize(using = IsoShortDateDeserializer.class)
        private Date endDate;
        private boolean required;
        private SurveyQuestion[] questions;
        @JsonProperty("dateStatus")
        private int status;

        public SurveyDto() {

        }

        public SurveyDto(Parcel in) {
            id = in.readString();
            name = in.readString();
            long date = in.readLong();
            startDate = date > 0 ? new Date(date) : null;
            date = in.readLong();
            endDate = date > 0 ? new Date(date) : null;
            required = in.readInt() != 0;
            questions = in.createTypedArray(SurveyQuestion.CREATOR);
            status = in.readInt();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(name);
            dest.writeLong(startDate != null ? startDate.getTime() : 0);
            dest.writeLong(endDate != null ? endDate.getTime() : 0);
            dest.writeInt(required ? 1 : 0);
            dest.writeTypedArray(questions, flags);
            dest.writeInt(status);
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Date getStartDate() {
            return startDate;
        }

        public void setStartDate(Date startDate) {
            this.startDate = startDate;
        }

        public Date getEndDate() {
            return endDate;
        }

        public void setEndDate(Date endDate) {
            this.endDate = endDate;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public boolean isRequired() {
            return required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }

        public SurveyQuestion[] getQuestions() {
            return questions;
        }

        public void setQuestions(SurveyQuestion[] questions) {
            this.questions = questions;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SurveyQuestion implements Parcelable {

        public static final Creator<SurveyQuestion> CREATOR = new Creator<SurveyQuestion>() {
            public SurveyQuestion createFromParcel(Parcel in) {
                return new SurveyQuestion(in);
            }

            public SurveyQuestion[] newArray(int size) {
                return new SurveyQuestion[size];
            }
        };

        private String id;
        private String name;
        private boolean multipleChoice;
        private boolean required;
        @JsonProperty("options")
        private SurveyAnswer[] answers;

        public SurveyQuestion() {

        }

        public SurveyQuestion(Parcel in) {
            id = in.readString();
            name = in.readString();
            multipleChoice = in.readInt() != 0;
            required = in.readInt() != 0;
            answers = in.createTypedArray(SurveyAnswer.CREATOR);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(name);
            dest.writeInt(multipleChoice ? 1 : 0);
            dest.writeInt(required ? 1 : 0);
            dest.writeTypedArray(answers, flags);
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isMultipleChoice() {
            return multipleChoice;
        }

        public void setMultipleChoice(boolean multipleChoice) {
            this.multipleChoice = multipleChoice;
        }

        public boolean isRequired() {
            return required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }

        public SurveyAnswer[] getAnswers() {
            return answers;
        }

        public void setAnswers(SurveyAnswer[] answers) {
            this.answers = answers;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SurveyAnswer implements Parcelable {

        public static final Creator<SurveyAnswer> CREATOR = new Creator<SurveyAnswer>() {
            public SurveyAnswer createFromParcel(Parcel in) {
                return new SurveyAnswer(in);
            }

            public SurveyAnswer[] newArray(int size) {
                return new SurveyAnswer[size];
            }
        };

        private String id;
        private String name;

        public SurveyAnswer() {

        }

        public SurveyAnswer(Parcel in) {
            id = in.readString();
            name = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(name);
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
