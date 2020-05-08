package com.viettel.dmsplus.sdk.network;

import com.viettel.dmsplus.sdk.SdkException;

public class TaskResult<T> {

    private T result;
    private SdkException exception;

    public TaskResult(T result) {
        this.result = result;
    }

    public TaskResult(SdkException exception) {
        this.exception = exception;
    }

    public boolean isSuccess() {
        return this.exception == null;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public SdkException getException() {
        return exception;
    }

    public void setException(SdkException exception) {
        this.exception = exception;
    }

}
