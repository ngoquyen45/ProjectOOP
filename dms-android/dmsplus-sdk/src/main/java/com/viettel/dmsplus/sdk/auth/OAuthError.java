package com.viettel.dmsplus.sdk.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Thanh
 */
public class OAuthError {

    private String error;

    @JsonProperty("error_description")
    private String description;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
