package com.viettel.backend.dto.feedback;

import java.io.Serializable;

public class FeedbackSimpleDto implements Serializable {

    private static final long serialVersionUID = 5051588225542322295L;

    private String createdTime;
    private String message;

    public FeedbackSimpleDto(String createdTime, String message) {
        super();

        this.createdTime = createdTime;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

}
