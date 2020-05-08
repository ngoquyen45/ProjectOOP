package com.viettel.dmsplus.sdk.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class that represents an error from server.
 */
public class ErrorInfo {

    @JsonProperty("error_type")
    private String type;

    private int code;

    @JsonProperty("error_message")
    private String message;

    public ErrorInfo() {

    }

    public ErrorInfo(String type, int code, String message) {
        this.type = type;
        this.code = code;
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class Envelope {

        private ErrorInfo meta;

        public ErrorInfo getMeta() {
            return meta;
        }

        public void setMeta(ErrorInfo meta) {
            this.meta = meta;
        }

    }

}