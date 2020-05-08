package com.viettel.backend.restful;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author thanh
 */
public class Meta {
    
    public static final Meta OK = new Meta(200);

    @JsonProperty("code")
    int code;

    public Meta() {
        super();
    }

    public Meta(int code) {
        super();
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
