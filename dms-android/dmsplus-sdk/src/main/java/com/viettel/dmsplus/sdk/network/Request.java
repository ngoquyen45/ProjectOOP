package com.viettel.dmsplus.sdk.network;

import android.annotation.TargetApi;
import android.os.Build;

import com.viettel.dmsplus.sdk.SdkConstants;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.auth.AuthenticationInfo;
import com.viettel.dmsplus.sdk.auth.OAuthSession;
import com.viettel.dmsplus.sdk.listeners.ProgressListener;
import com.viettel.dmsplus.sdk.utils.LogUtils;
import com.viettel.dmsplus.sdk.utils.SdkUtils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This class represents a request made to the server.
 * @param <T> The object that data from the server should be parsed into.
 */
public class Request<T> {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;

    private static final ThreadPoolExecutor REQUEST_EXECUTOR = SdkUtils.createDefaultThreadPoolExecutor(
            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, 3600, TimeUnit.SECONDS);

    protected String            mRequestUrl;
    protected HttpMethod        mRequestMethod;
    protected Object[]          mRequestParams;
    protected Object            mRequestBody;
    protected HttpHeaders       mRequestHeaders;

    protected OAuthSession      mSession;
    protected ProgressListener  mListener;

    protected ErrorHandler      mErrorHandler;
    protected Class<T>          mClazz;

    protected boolean           mSkipAuthError;

    /**
     * Constructs a new Request.
     * @param clazz The class of the object that should be returned, the class specified by the child in T.
     * @param requestUrl the url to use to connect to server.
     * @param session the session used to authenticate the given request.
     */
    public Request(Class<T> clazz, String requestUrl, OAuthSession session) {
        mClazz = clazz;
        mRequestUrl = requestUrl;
        mSession = session;
        mRequestMethod = HttpMethod.GET;
        mSkipAuthError = false;
        setErrorHandler(ErrorHandler.getInstance());
    }

    /**
     * Gets session used to authenticate this request.
     *
     * @return the session used to authenticate this request.
     */
    public OAuthSession getSession() {
        return mSession;
    }

    /**
     * Gets the request handler.
     *
     * @return  request handler.
     */
    public ErrorHandler getErrorHandler() {
        return mErrorHandler;
    }

    public Request<T> setErrorHandler(ErrorHandler handler) {
        mErrorHandler = handler;
        return this;
    }

    public Request<T> setSkipAuthError(boolean skipAuthError) {
        this.mSkipAuthError = skipAuthError;
        return this;
    }

    private void addSecureHeader() {
        if (mSession == null) {
            return;
        }
        AuthenticationInfo info = mSession.getAuthInfo();
        String accessToken = (info == null ? null : info.getAccessToken());
        if (!SdkUtils.isEmptyString(accessToken)) {
            if (mRequestHeaders == null) {
                mRequestHeaders = new HttpHeaders();
            }
            mRequestHeaders.set("Authorization", String.format(Locale.ENGLISH, "Bearer %s", accessToken));
        }
    }

    /**
     * Synchronously make the request to server and handle the response appropriately.
     * @return the expected Object if the request is successful.
     * @throws SdkException thrown if there was a problem with handling the request.
     */
    public T execute() throws SdkException {
        ErrorHandler requestHandler = getErrorHandler();
        try {
            // Add Bearer token header
            addSecureHeader();

            logRequest();

            HttpEntity<?> entity = new HttpEntity<Object>(mRequestBody, mRequestHeaders);
            ResponseEntity<T> response;

            /*quang fix token for query*/
            if (mRequestUrl.contains("calculate") == true) {

                 response = RestTemplate.get().exchange(mRequestUrl,
                        mRequestMethod, entity, mClazz, mRequestParams);

            }
            else {
                response = RestTemplate.get().exchange(mRequestUrl,
                        mRequestMethod, entity, mClazz, mRequestParams);
            }

            logDebug(response);
            return response.getBody();

        } catch (CustomHttpClientErrorException e) {
            return handleClientException(requestHandler, e);
        } catch (CustomHttpServerErrorException e) {
            return handleServerException(requestHandler, e);
        } catch (HttpMessageNotReadableException e) {
            return handleSendException(requestHandler, e);
        } catch (ResourceAccessException e) {
            return handleSendException(requestHandler, e);
        } catch (RestClientException e) {
            return handleSendException(requestHandler, e);
        }
    }

    /**
     * Creates a SdkAsyncTask to make the request asynchronously. Callback will be invoke in UI thread
     */
    public SdkAsyncTask<T> executeAsync(RequestCompleteCallback<T> listener) {
        return executeAsync(listener, REQUEST_EXECUTOR);
    }

    /**
     * Creates a SdkAsyncTask to make the request asynchronously. Callback will be invoke in UI thread.
     * This call required Android version >= 11. On lower version, ExecutorService param will be ignored
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SdkAsyncTask<T> executeAsync(RequestCompleteCallback<T> listener, ExecutorService executor) {
        final int sdk = Build.VERSION.SDK_INT;
        SdkAsyncTask<T> task = new SdkAsyncTask<T>(this, listener);
        if (sdk >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(executor);
        } else {
            task.execute();
        }
        return task;
    }

    /**
     * Creates a SdkFutureTask to make the request asynchronously. Callback will be invoke in background thread
     */
    public SdkFutureTask<T> executeAsyncOnBackground(RequestCompleteCallback<T> listener) {
        return executeAsyncOnBackground(listener, REQUEST_EXECUTOR);
    }

    /**
     * Creates a SdkFutureTask to make the request asynchronously. Callback will be invoke in background thread
     */
    public SdkFutureTask<T> executeAsyncOnBackground(RequestCompleteCallback<T> listener, ExecutorService executor) {
        SdkFutureTask<T> task = new SdkFutureTask<T>(this, listener);
        executor.submit(task);
        return task;
    }

    private T handleClientException(ErrorHandler requestHandler, CustomHttpClientErrorException ex) throws SdkException {
        LogUtils.e(SdkConstants.TAG, "Request failed with exception", ex);
        SdkException sdkException = new SdkException("Couldn't connect to the server due to a client error.", ex);
        if (requestHandler.onException(this, sdkException)) {
            return execute();
        } else {
            throw sdkException;
        }
    }

    private T handleServerException(ErrorHandler requestHandler, CustomHttpServerErrorException ex) throws SdkException {
        LogUtils.e(SdkConstants.TAG, "Request failed with exception", ex);
        throw new SdkException("Couldn't connect to the server due to a server error.", ex);
    }

    private T handleSendException(ErrorHandler requestHandler, Exception ex) throws SdkException {
        LogUtils.e(SdkConstants.TAG, "Request failed with exception", ex);
        if (ex instanceof SdkException) {
            throw (SdkException) ex;
        } else {
            throw new SdkException("Couldn't connect to the server due to a network error.", ex);
        }
    }

    protected void logDebug(ResponseEntity<T> response) throws SdkException {
        LogUtils.i(SdkConstants.TAG, String.format(Locale.ENGLISH, "Response (%s):  %s", response.getStatusCode(), response.getBody()));
    }

    protected void logRequest() {
        LogUtils.i(SdkConstants.TAG, String.format(Locale.ENGLISH, "Request (%s):  %s", mRequestMethod, mRequestUrl));
        LogUtils.i(SdkConstants.TAG, "Request Header", mRequestHeaders == null ? null: mRequestHeaders.toSingleValueMap());
        LogUtils.i(SdkConstants.TAG, "Request Data: " + (mRequestBody == null ? null : mRequestBody.toString()));
    }

    public HttpMethod getRequestMethod() {
        return mRequestMethod;
    }

    public void setRequestMethod(HttpMethod requestMethod) {
        this.mRequestMethod = requestMethod;
    }

    public Object[] getRequestParams() {
        return mRequestParams;
    }

    public void setRequestParams(Object[] requestParams) {
        this.mRequestParams = requestParams;
    }

    public Object getRequestBody() {
        return mRequestBody;
    }

    public void setRequestBody(Object requestBody) {
        this.mRequestBody = requestBody;
    }

    public HttpHeaders getRequestHeaders() {
        return mRequestHeaders;
    }

    public void setRequestHeaders(HttpHeaders requestHeaders) {
        this.mRequestHeaders = requestHeaders;
    }

}
