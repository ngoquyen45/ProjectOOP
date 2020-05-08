package com.viettel.dmsplus.sdk.network;

import com.viettel.dmsplus.sdk.ErrorType;
import com.viettel.dmsplus.sdk.SdkConstants;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.auth.OAuthSession;
import com.viettel.dmsplus.sdk.utils.LogUtils;

import java.util.concurrent.ExecutionException;

public class ErrorHandler {

    private static ErrorHandler instance;

    public static ErrorHandler getInstance() {
        if (instance == null) {
            synchronized (ErrorHandler.class) {
                if (instance == null) {
                    instance = new ErrorHandler();
                }
            }
        }
        return instance;
    }

    /**
     * @return true if exception is handled well and request can be re-sent. false otherwise.
     */
    public boolean onException(Request request, SdkException ex) throws SdkException {
        OAuthSession session = request.getSession();

        // If no session (eg: Request token using 'password' flow) or the request ask for skip further
        // processing, ignore that
        if (session == null) {
            return false;
        }

        // If normal request, try to recover the error
        if (oauthExpired(ex)) {
            try {
                TaskResult<OAuthSession> refreshResponse = session.refresh().get();
                if (refreshResponse.isSuccess()) {
                    return true;
                } else if (refreshResponse.getException() != null) {
                    throw refreshResponse.getException();
                }
            } catch (InterruptedException e){
                LogUtils.e("oauthRefresh", "Interrupted Exception", e);
            } catch (ExecutionException e1){
                LogUtils.e("oauthRefresh", "Interrupted Exception", e1);
            }
        } else if (authFailed(ex)) {
            LogUtils.e(SdkConstants.TAG, "Faltal error", ex);
            session.getAuthInfo().setUserInfo(null);
            if (request.mSkipAuthError) {
                return false;
            }
//            try {
//                session.authenticate(null).get();
//                return session.getUserInfo() != null;
//            } catch (Exception e) {
//                //  return false;
//            }
            session.onAuthFailure(session.getAuthInfo(), ex);
        }
        return false;
    }

    private boolean authFailed(SdkException ex) {
        return ex.isErrorFatal();
    }

    private boolean oauthExpired(SdkException ex) {
        return ex.getErrorType() == ErrorType.INVALID_ACCESS_TOKEN;
    }

}
