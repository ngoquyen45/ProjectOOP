package com.viettel.dmsplus.sdk;

import com.viettel.dmsplus.sdk.auth.OAuthSession;
import com.viettel.dmsplus.sdk.network.Request;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;

/**
 * Base class for the API endpoint classes.
 * @author Thanh
 */
public class Endpoint {

    protected OAuthSession mSession;

    protected String getApiBaseUrl() {
        return SdkConfig.getApiBaseUrl();
    }

    /**
     * Constructs a Endpoint with the provided OAuthSession.
     *
     * @param session authenticated session to use with the Endpoint.
     */
    public Endpoint(OAuthSession session) {
        this.mSession = session;
    }

    public <T> Request<T> get(final String url,
                                 final Class<T> clazz, final Object... params) {
        return get(url, clazz, null, params);
    }

    public <T> Request<T> get(final String url,
                                 final Class<T> clazz, final HttpHeaders headers,
                                 final Object... params) {
        return exchange(HttpMethod.GET, url, null, clazz, headers, params);
    }

    public <T> Request<T> post(final String url,
                                  final Object content, final Class<T> clazz, final Object... params) {
        return post(url, content, clazz, null, params);
    }

    public <T> Request<T> post(final String url,
                                  final Class<T> clazz, final HttpHeaders headers,
                                  final Object... params) {
        return post(url, null, clazz, headers, params);
    }

    public <T> Request<T> post(final String url,
                                  final Object content, final Class<T> clazz,
                                  final HttpHeaders headers, final Object... params) {
        return exchange(HttpMethod.POST, url, content, clazz, headers, params);
    }

    public <T> Request<T> put(final String url,
                                 final Object content, final Class<T> clazz, final Object... params) {
        return put(url, content, clazz, null, params);
    }

    public <T> Request<T> put(final String url,
                                 final Class<T> clazz, final MultiValueMap<String, String> headers,
                                 final Object... params) {
        return put(url, null, clazz, headers, params);
    }

    public <T> Request<T> put(final String url,
                                 final Object content, final Class<T> clazz,
                                 final HttpHeaders headers, final Object... params) {
        return exchange(HttpMethod.PUT, url, content, clazz, headers, params);
    }

    public <T> Request<T> delete(final String url,
                                    final Object content, final Class<T> clazz, final Object... params) {
        return delete(url, content, clazz, null, params);
    }

    public <T> Request<T> delete(final String url,
                                    final Class<T> clazz, final HttpHeaders headers,
                                    final Object... params) {
        return delete(url, null, clazz, headers, params);
    }

    public <T> Request<T> delete(final String url,
                                    final Object content, final Class<T> clazz,
                                    final HttpHeaders headers, final Object... params) {
        return exchange(HttpMethod.DELETE, url, content, clazz, headers, params);
    }

    public <T> Request<T> exchange(
            final HttpMethod method, final String url,
            final Object content, final Class<T> clazz,
            final HttpHeaders headers, final Object... params) {
        Request<T> request = new Request<T>(clazz, url, mSession);
        request.setRequestMethod(method);
        request.setRequestBody(content);
        request.setRequestHeaders(headers);
        request.setRequestParams(params);
        return request;
    }

}
