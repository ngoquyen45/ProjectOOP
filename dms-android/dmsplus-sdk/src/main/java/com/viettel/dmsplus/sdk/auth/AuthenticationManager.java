package com.viettel.dmsplus.sdk.auth;

import android.content.Context;
import android.content.Intent;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.viettel.dmsplus.sdk.ErrorType;
import com.viettel.dmsplus.sdk.MainEndpoint;
import com.viettel.dmsplus.sdk.SdkConfig;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.ErrorInfo;
import com.viettel.dmsplus.sdk.models.UserInfo;
import com.viettel.dmsplus.sdk.network.Request;
import com.viettel.dmsplus.sdk.utils.SdkUtils;

/**
 * Handles authentication and stores authentication information.
 */
public class AuthenticationManager {

    // Third parties who are looking to provide their own refresh logic should replace this with the constructor that takea refreshProvider.
    private static AuthenticationManager mAuthentication = new AuthenticationManager();

    private ConcurrentLinkedQueue<WeakReference<AuthenticationListener>> mListeners = new ConcurrentLinkedQueue<WeakReference<AuthenticationListener>>();

    private ConcurrentHashMap<String, AuthenticationInfo> mCurrentAccessInfo;

    private ConcurrentHashMap<String, FutureTask> mRefreshingTasks = new ConcurrentHashMap<String, FutureTask>();

    public static final ThreadPoolExecutor AUTH_EXECUTOR =  SdkUtils.createDefaultThreadPoolExecutor(1, 20, 3600, TimeUnit.SECONDS);

    private AuthenticationRefreshProvider mRefreshProvider;

    private AuthenticationStorage authStorage = new AuthenticationStorage();

    private AuthenticationManager() {
    }

    private AuthenticationManager(final AuthenticationRefreshProvider refreshProvider) {
        mRefreshProvider = refreshProvider;
    }

    /**
     * Get the AuthenticationInfo for a given user.
     */
    public AuthenticationInfo getAuthInfo(String userId, Context context) {
        return userId == null ? null : getAuthInfoMap(context).get(userId);
    }

    /**
     * Get a map of all stored auth information.
     * @param context current context.
     * @return a map with all stored user information, or null if no user info has been stored.
     */
    public Map<String, AuthenticationInfo> getStoredAuthInfo(final Context context){
        return getAuthInfoMap(context);
    }

    /**
     * Get the user id that was last authenticated.
     * @param context current context.
     * @return the user id that was last authenticated or null if it does not exist or was removed.
     */
    public String getLastAuthenticatedUserId(final Context context){
        return authStorage.getLastAuthentictedUserId(context);
    }

    /**
     * Get singleton instance of the AuthenticationManager object.
     */
    public static AuthenticationManager getInstance() {
        return mAuthentication;
    }

    public AuthenticationRefreshProvider getRefreshProvider() {
        return mRefreshProvider;
    }

    public void setRefreshProvider(AuthenticationRefreshProvider refreshProvider) {
        this.mRefreshProvider = refreshProvider;
    }

    /**
     * Set the storage to store auth information. By default, sharedpref is used. You can use this method to use your own storage class that extends the AuthenticationStorage.
     */
    public void setAuthStorage(AuthenticationStorage storage) {
        this.authStorage = storage;
    }

    /**
     * Get the auth storage used to store auth information.
     */
    public AuthenticationStorage getAuthStorage() {
        return authStorage;
    }

    public synchronized void startAuthenticationUI(OAuthSession session) {
        startAuthenticateUI(session);
    }

    /**
     * Callback method to be called when authentication process finishes.
     */
    public synchronized void onAuthenticated(AuthenticationInfo info, Context context) {
        getAuthInfoMap(context).put(info.getUserInfo().getId(), info.clone());
        authStorage.storeLastAuthenticatedUserId(info.getUserInfo().getId(), context);
        authStorage.storeAuthInfoMap(mCurrentAccessInfo, context);
        // if accessToken has not already been refreshed, issue refresh request and cache result
        for (WeakReference<AuthenticationListener> reference : mListeners) {
            AuthenticationListener rc = reference.get();
            if (rc != null) {
                rc.onAuthCreated(info);
            }
        }
    }

    /**
     * Callback method to be called if authentication process fails.
     */
    public synchronized void onAuthenticationFailure(AuthenticationInfo info, Exception ex) {
        for (WeakReference<AuthenticationListener> reference : mListeners) {
            AuthenticationListener rc = reference.get();
            if (rc != null) {
                rc.onAuthFailure(info, ex);
            }
        }
    }

    /**
     * Callback method to be called on logout.
     */
    public synchronized void onLoggedOut(AuthenticationInfo info, Exception ex) {
        for (WeakReference<AuthenticationListener> reference : mListeners) {
            AuthenticationListener rc = reference.get();
            if (rc != null) {
                rc.onLoggedOut(info, ex);
            }
        }
    }

    /**
     * Log out current OAuthSession. After logging out, the authentication information related to the user in this session will be gone.
     */
    public synchronized void logout(OAuthSession session) throws SdkException {
        UserInfo user = session.getUserInfo();
        if (user == null) {
            return;
        }

        Context context = session.getApplicationContext();
        String userId = user.getId();

        getAuthInfoMap(session.getApplicationContext());
        AuthenticationInfo info = mCurrentAccessInfo.get(userId);

        OAuthEndpoint endpoint = new OAuthEndpoint(session);
        endpoint.revokeAccessToken(info.getAccessToken()).executeAsyncOnBackground(null);
        info.wipeOutAuth();
        mCurrentAccessInfo.remove(userId);
        if (authStorage.getLastAuthentictedUserId(context).equals(userId)){
            authStorage.storeLastAuthenticatedUserId(null, context);
        }
        authStorage.storeAuthInfoMap(mCurrentAccessInfo, context);
        if (OAuthSession.getDefaultSession() != null && OAuthSession.getDefaultSession().getUserId().equals(userId)) {
            OAuthSession.setDefaultSession(null);
            MainEndpoint.reset();
        }
    }

    /**
     * Log out all users. After logging out, all authentication information will be gone.
     */
    public synchronized void logoutAllUsers(Context context) {
        getAuthInfoMap(context);
        for (String userId : mCurrentAccessInfo.keySet()) {
            AuthenticationInfo info = mCurrentAccessInfo.get(userId);

            if (info.getClientId() != null) {
                if (info.getClientId().equals(SdkConfig.CLIENT_ID)) {
                    try {
                        OAuthEndpoint endpoint = new OAuthEndpoint(new OAuthSession(context, userId));
                        endpoint.revokeAccessToken(info.getAccessToken()).executeAsyncOnBackground(null);
                        onLoggedOut(info.clone(), null);
                        info.wipeOutAuth();
                    } catch (SdkException e) {
                        e.printStackTrace();
                        onLoggedOut(info, e);
                    }
                } else {
                    onLoggedOut(info, new NonDefaultClientLogoutException());
                }
            }
        }
        mCurrentAccessInfo.clear();
        authStorage.storeLastAuthenticatedUserId(null, context);
        authStorage.clearAuthInfoMap(context);

        OAuthSession.setDefaultSession(null);
        MainEndpoint.reset();
    }

    /**
     * Refresh the OAuth in the given OAuthSession. This method is called when OAuth token expires.
     */
    public synchronized FutureTask<AuthenticationInfo> refresh(OAuthSession session) throws SdkException {
        UserInfo user = session.getUserInfo();
        if (user == null){
            return doRefresh(session, session.getAuthInfo());
        }
        // Fetch auth info map from storage if not present.
        getAuthInfoMap(session.getApplicationContext());
        AuthenticationInfo info = mCurrentAccessInfo.get(user.getId());

        if (info == null) {
            // session has info that we do not. ? is there any other situation we want to update our info based on session info? we can do checks against
            // refresh time.
            mCurrentAccessInfo.put(user.getId(), session.getAuthInfo());
            info = mCurrentAccessInfo.get(user.getId());
        }

        if (!session.getAuthInfo().getAccessToken().equals(info.getAccessToken())) {
            final AuthenticationInfo latestInfo = info;
            // this session is probably using old information. Give it our information.
            AuthenticationInfo.cloneInfo(session.getAuthInfo(), info);
            return new FutureTask<AuthenticationInfo>(new Callable<AuthenticationInfo>(){
                @Override
                public AuthenticationInfo call() throws Exception {
                    return latestInfo;
                }
            });
        }

        FutureTask task = mRefreshingTasks.get(user.getId());
        if (task != null) {
            // We already have a refreshing task for this user. No need to do anything.
            // noinspection unchecked
            return task;
        }

        // create the task to do the refresh and put it in mRefreshingTasks and execute it.
        return doRefresh(session, info);

    }

    /**
     * Add listener to listen to the authentication process for this OAuthSession.
     */
    public synchronized void addListener(AuthenticationListener listener) {
        mListeners.add(new WeakReference<AuthenticationListener>(listener));
    }

    /**
     * Start authentication UI.
     * @param session the session to authenticate.
     */
    protected synchronized void startAuthenticateUI(OAuthSession session) {
        Context context = session.getApplicationContext();
        Intent intent = OAuthActivity.createOAuthActivityIntent(context, session);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private SdkException handleRefreshException(final SdkException e, final AuthenticationInfo info) {
        AuthenticationManager.getInstance().onAuthenticationFailure(info, e);
        return e;
    }

    private FutureTask<AuthenticationInfo> doRefresh(final OAuthSession session, final AuthenticationInfo info) throws SdkException {
        final boolean userUnknown = (info.getUserInfo() == null && session.getUserInfo() == null);
        final String taskKey = SdkUtils.isBlank(session.getUserId()) && userUnknown ? info.getAccessToken() : session.getUserId();
        FutureTask<AuthenticationInfo> task = new FutureTask<AuthenticationInfo>(new Callable<AuthenticationInfo>() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public AuthenticationInfo call() throws Exception {
                try {
                    AuthenticationInfo refreshInfo;
                    OAuthEndpoint oauthEndpoint = new OAuthEndpoint(session);
                    if (session.getRefreshProvider() != null) {
                        try {
                            refreshInfo = session.getRefreshProvider().refreshAuthenticationInfo(info);
                        } catch (SdkException e) {
                            throw handleRefreshException(e, info);
                        }
                    } else if (mRefreshProvider != null) {
                        try {
                            refreshInfo = mRefreshProvider.refreshAuthenticationInfo(info);
                        } catch (SdkException e) {
                            throw handleRefreshException(e, info);
                        }
                    } else {
                        String refreshToken = info.getRefreshToken() != null ? info.getRefreshToken() : "";
                        String clientId = session.getClientId() != null ? session.getClientId() : SdkConfig.CLIENT_ID;
                        String clientSecret = session.getClientSecret() != null ? session.getClientSecret() : SdkConfig.CLIENT_SECRET;
                        if (SdkUtils.isBlank(clientId) || SdkUtils.isBlank(clientSecret)) {
                            SdkException badRequest = new SdkException("client id or secret not specified", new ErrorInfo("invalid_client", 400, "client id or secret not specified"), ErrorType.OAUTH_ERROR);
                            throw handleRefreshException(badRequest, info);
                        }

                        Request<AuthenticationInfo> request = oauthEndpoint.refreshAccessToken(refreshToken, clientId, clientSecret);
                        try {
                            refreshInfo = request.execute();
                            refreshInfo.setUserInfo(session.getUserInfo());
                        } catch (SdkException e) {
                            throw handleRefreshException(e, info);
                        }
                    }
                    if (refreshInfo != null) {
                        refreshInfo.setRefreshTime(System.currentTimeMillis());
                        refreshInfo.setClientId(session.getClientId());
                    }
                    AuthenticationInfo.cloneInfo(session.getAuthInfo(), refreshInfo);
                    // if we using a custom refresh provider ensure we check the user, otherwise do this only if we don't know who the user is.
                    if (userUnknown || session.getRefreshProvider() != null || mRefreshProvider != null) {
                        info.setUserInfo(oauthEndpoint.getUserInfo().execute());
                    }

                    getAuthInfoMap(session.getApplicationContext()).put(info.getUserInfo().getId(), refreshInfo);
                    authStorage.storeAuthInfoMap(mCurrentAccessInfo, session.getApplicationContext());
                    // call notifyListeners() with results.
                    for (WeakReference<AuthenticationListener> reference : mListeners) {
                        AuthenticationListener rc = reference.get();
                        if (rc != null) {
                            rc.onRefreshed(refreshInfo);
                        }
                    }
                    if (!session.getUserId().equals(info.getUserInfo().getId())) {
                        session.onAuthFailure(info, new SdkException("Session UserInfo Id has changed!"));
                    }

                    return info;
                } finally {
                    mRefreshingTasks.remove(taskKey);
                }
            }
        });
        mRefreshingTasks.put(taskKey, task);
        AUTH_EXECUTOR.execute(task);
        return task;
    }

    private ConcurrentHashMap<String, AuthenticationInfo> getAuthInfoMap(Context context) {
        if (mCurrentAccessInfo == null) {
            mCurrentAccessInfo = authStorage.loadAuthInfoMap(context);
        }
        return mCurrentAccessInfo;
    }

    /**
     * Exception if user cannot be logged out using the default SdkConfig client id/secret.
     */
    public static class NonDefaultClientLogoutException extends Exception {

        public NonDefaultClientLogoutException() {
            super();
        }
    }

}
