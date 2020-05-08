package com.viettel.dms.helper.network;

import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;

/**
 * @author thanh
 * @since 10/8/15
 */
public class RequestInfo<T> {

    private HttpMethod method;
    private String url;
    private Object content;
    private Class<T> clazz;
    private MultiValueMap<String, String> headers;
    private Object[] params;

    public RequestInfo(HttpMethod method, String url) {
        this.method = method;
        this.url = url;
    }

    public RequestInfo(HttpMethod method, String url, Object content, Class<T> clazz, MultiValueMap<String, String> headers, Object[] params) {
        this.method = method;
        this.url = url;
        this.content = content;
        this.clazz = clazz;
        this.headers = headers;
        this.params = params;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public void setClazz(Class<T> clazz) {
        this.clazz = clazz;
    }

    public MultiValueMap<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(MultiValueMap<String, String> headers) {
        this.headers = headers;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }
}
