package com.viettel.dmsplus.sdk.network;

import com.viettel.dmsplus.sdk.SdkException;

public abstract class RequestCompleteCallback<E> {

    public abstract void onSuccess(E response);

    public abstract void onError(SdkException ex);

    public void onFinish(boolean canceled) { }

}