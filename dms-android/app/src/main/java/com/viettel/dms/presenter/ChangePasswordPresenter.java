package com.viettel.dms.presenter;

import com.viettel.dms.ui.iview.IChangePasswordView;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.auth.OAuthEndpoint;
import com.viettel.dmsplus.sdk.auth.OAuthSession;
import com.viettel.dmsplus.sdk.models.ErrorInfo;
import com.viettel.dmsplus.sdk.network.RequestCompleteCallback;
import com.viettel.dmsplus.sdk.network.SdkAsyncTask;

/**
 * @author PHAMHUNG
 * @since 9/11/2015
 */
public class ChangePasswordPresenter extends BasePresenter {

    private IChangePasswordView iView;
    private SdkAsyncTask<?> mTask;

    public ChangePasswordPresenter(IChangePasswordView i) {
        iView = i;
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mTask != null) {
            mTask.cancel(true);
            mTask = null;
        }
    }

    public void requestChangePassword(String oldPassword, String newPassword) {
        RequestCompleteCallback<ErrorInfo> mCallback = new RequestCompleteCallback<ErrorInfo>() {
            @Override
            public void onSuccess(ErrorInfo data) {
                iView.changePasswordSuccess();
            }

            @Override
            public void onError(SdkException info) {
                iView.changePasswordError(info);
            }

            @Override
            public void onFinish(boolean canceled) {
                mTask = null;
            }
        };
        mTask = new OAuthEndpoint(OAuthSession.getDefaultSession())
                .requestChangePassword(oldPassword, newPassword)
                .executeAsync(mCallback);
    }
}
