package com.viettel.dmsplus.sdk.network;

import android.os.AsyncTask;

import com.viettel.dmsplus.sdk.SdkException;

public class SdkAsyncTask<E> extends AsyncTask<Void, Void, TaskResult<E>> {

    private Request<E> mRequest;
    private RequestCompleteCallback<E> mCompletedListener;

    public SdkAsyncTask(final Request<E> request, RequestCompleteCallback<E> listener) {
        this.mRequest = request;
        this.mCompletedListener = listener;
    }

    @Override
    protected TaskResult<E> doInBackground(Void... params) {
        try {
            return new TaskResult<E>(mRequest.execute());
        } catch (Exception e) {
            if (e instanceof SdkException) {
                return new TaskResult<E>((SdkException) e);
            } else {
                return new TaskResult<E>(new SdkException("Exception when communicate with server", e));
            }

        }
    }

    @Override
    protected void onPostExecute(TaskResult<E> taskResult) {
        if (mCompletedListener != null) {
            if (!isCancelled()) {
                if (taskResult.isSuccess()) {
                    mCompletedListener.onSuccess(taskResult.getResult());
                } else {
                    mCompletedListener.onError(taskResult.getException());
                }
            }
            mCompletedListener.onFinish(isCancelled());
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        mCompletedListener.onFinish(true);
    }
}
