package com.viettel.dms.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.viettel.dms.DMSApplication;
import com.viettel.dms.helper.share.DMSSharePreference;
import com.viettel.dms.ui.iview.ILoginView;
import com.viettel.dmsplus.sdk.Role;
import com.viettel.dmsplus.sdk.SdkConfig;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.auth.AuthenticationInfo;
import com.viettel.dmsplus.sdk.auth.AuthenticationManager;
import com.viettel.dmsplus.sdk.auth.OAuthEndpoint;
import com.viettel.dmsplus.sdk.auth.OAuthSession;
import com.viettel.dmsplus.sdk.models.UserInfo;
import com.viettel.dmsplus.sdk.network.RequestCompleteCallback;
import com.viettel.dmsplus.sdk.network.SdkAsyncTask;

/**
 * @author PHAMHUNG
 * @since 8/17/2015
 */
public class LoginPresenter extends BasePresenter {

    private ILoginView iLoginView;
    private SdkAsyncTask<?> task;
    private OAuthSession mSession;

    private String username;

    public LoginPresenter(ILoginView iView) {
        iLoginView = iView;
    }

    @Override
    public void onStop() {
        if (task != null) {
            task.cancel(true);
        }
    }

    public void checkLoginStatus() {
        Context context = DMSApplication.get();
        String userId = AuthenticationManager.getInstance().getLastAuthenticatedUserId(context);

        if (!TextUtils.isEmpty(userId)) {
            mSession = new OAuthSession(context, userId);
            continueToMainMenu();
        } else {
            DMSApplication.get().logout(context, false);
            iLoginView.showLayoutLogin();
        }
    }

    public void doLoginTask(String username, String password) {
        this.username = username;

        this.mSession = new OAuthSession(DMSApplication.get(), null);

        task = new OAuthEndpoint(mSession)
                .requestAccessToken(username, password, SdkConfig.CLIENT_ID, SdkConfig.CLIENT_SECRET)
                .setSkipAuthError(true)
                .executeAsync(loginCallback);
    }

    RequestCompleteCallback<AuthenticationInfo> loginCallback = new RequestCompleteCallback<AuthenticationInfo>() {
        @Override
        public void onSuccess(AuthenticationInfo auth) {
            if (auth == null) {
                onError(new SdkException("Unknow error"));
                return;
            }
            AuthenticationInfo sessionAuth = mSession.getAuthInfo();

            sessionAuth.setAccessToken(auth.getAccessToken());
            sessionAuth.setRefreshToken(auth.getRefreshToken());
            sessionAuth.setRefreshTime(System.currentTimeMillis());
            sessionAuth.setClientId(mSession.getClientId());

            DMSSharePreference.get().putLastAuthenticatedUsername(username);
            DMSSharePreference.get().putLastUserThemeKey(DMSSharePreference.KEY_THEME_USER + username);

            continueToMainMenu();
        }

        @Override
        public void onError(SdkException info) {
            iLoginView.processError(info);
        }

        @Override
        public void onFinish(boolean canceled) {
            task = null;
        }
    };

    public void continueToMainMenu() {
        RequestCompleteCallback<UserInfo> callback = new RequestCompleteCallback<UserInfo>() {

            @Override
            public void onSuccess(UserInfo userInfo) {
                Context context = DMSApplication.get();

                mSession.setUserId(userInfo.getId());
                mSession.getAuthInfo().setUserInfo(userInfo);
                AuthenticationManager.getInstance().onAuthenticated(mSession.getAuthInfo(), context);
                OAuthSession.setDefaultSession(mSession);

                if (userInfo.getRole() == Role.SALESMAN) {
                    iLoginView.startMainActivity();
                } else {
                    iLoginView.forceLogout();
                }
            }

            @Override
            public void onError(SdkException info) {
                iLoginView.processError(info);
            }

            @Override
            public void onFinish(boolean canceled) {
                task = null;
            }
        };
        task = new OAuthEndpoint(mSession)
                .getUserInfo()
                .setSkipAuthError(true)
                .executeAsync(callback);
    }

}
