package com.viettel.dmsplus.sdk.network;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.charset.Charset;

/**
 * @author Thanh
 */
public class CustomHttpClientErrorException extends HttpClientErrorException {

    private HttpHeaders headers;

    public CustomHttpClientErrorException(HttpStatus statusCode) {
        super(statusCode);
    }

    public CustomHttpClientErrorException(HttpStatus statusCode, String statusText) {
        super(statusCode, statusText);
    }

    public CustomHttpClientErrorException(HttpStatus statusCode, String statusText, byte[] responseBody, Charset responseCharset, HttpHeaders headers) {
        super(statusCode, statusText, responseBody, responseCharset);
        this.headers = headers;
    }

    public HttpHeaders getHeaders() {
        return this.headers;
    }

}
