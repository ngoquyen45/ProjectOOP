package com.viettel.dmsplus.sdk.network;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import com.viettel.dmsplus.sdk.SdkException;

/**
 * @param <E> entity type returned from request
 */
public class SdkFutureTask<E> extends FutureTask<TaskResult<E>> {

    protected RequestCompleteCallback<E> mCompletedListener = null;

    public SdkFutureTask(final Request<E> request, RequestCompleteCallback<E> listener) {
        super(new Callable<TaskResult<E>>() {

            @Override
            public TaskResult<E> call() throws Exception {
                try {
                    return new TaskResult<E>(request.execute());
                } catch (Exception e) {
                    if (e instanceof SdkException) {
                        return new TaskResult<E>((SdkException) e);
                    } else {
                        return new TaskResult<E>(new SdkException("Exception when communicate with server", e));
                    }
                }
            }
        });
        mCompletedListener = listener;
    }

    @Override
    protected void done() {
        TaskResult<E> response = null;
        Exception ex = null;
        try {
            response = this.get();
        } catch (InterruptedException e) {
            ex = e;
        } catch (ExecutionException e) {
            ex = e;
        }

        if (ex != null) {
            response = new TaskResult<E>(new SdkException("Unable to retrieve response from FutureTask.", ex));
        }

        if (mCompletedListener != null) {
            if (!isCancelled()) {
                if (response.isSuccess()) {
                    mCompletedListener.onSuccess(response.getResult());
                } else {
                    mCompletedListener.onError(response.getException());
                }
            }
            mCompletedListener.onFinish(isCancelled());
        }
    }

}
