package com.viettel.dmsplus.sdk.network;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

import java.nio.charset.Charset;

/**
 * @author Thanh
 */
public class CustomHttpServerErrorException extends HttpServerErrorException {

    private HttpHeaders headers;

    public CustomHttpServerErrorException(HttpStatus statusCode) {
        super(statusCode);
    }

    public CustomHttpServerErrorException(HttpStatus statusCode, String statusText) {
        super(statusCode, statusText);
    }

    public CustomHttpServerErrorException(HttpStatus statusCode, String statusText, byte[] responseBody, Charset responseCharset, HttpHeaders headers) {
        super(statusCode, statusText, responseBody, responseCharset);
        this.headers = headers;
    }

    public HttpHeaders getHeaders() {
        return this.headers;
    }

}
