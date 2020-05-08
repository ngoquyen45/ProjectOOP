package com.viettel.dmsplus.sdk.auth;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.viettel.dmsplus.sdk.ErrorType;
import com.viettel.dmsplus.sdk.Role;
import com.viettel.dmsplus.sdk.SdkConfig;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.UserInfo;
import com.viettel.dmsplus.sdk.network.RequestCompleteCallback;
import com.viettel.dmsplus.sdk.network.SdkFutureTask;
import com.viettel.dmsplus.sdk.network.Request;
import com.viettel.dmsplus.sdk.utils.LogUtils;
import com.viettel.dmsplus.sdk.utils.SdkUtils;
import com.viettel.dmsplus.sdk.R;

/**
 * A OAuthSession is responsible for maintaining the mapping between user and authentication tokens
 */
public class OAuthSession implements AuthenticationListener {

    private static final ThreadPoolExecutor AUTH_CREATION_EXECUTOR = SdkUtils.createDefaultThreadPoolExecutor(1, 20, 3600, TimeUnit.SECONDS);
    private static OAuthSession DEFAULT_SESSION;

    public static OAuthSession getDefaultSession() {
        return DEFAULT_SESSION;
    }

    public static void setDefaultSession(OAuthSession defaultSession) {
        DEFAULT_SESSION = defaultSession;
    }

    private String mUserAgent = "Android SDK";
    private Context mApplicationContext;
    private AuthenticationListener sessionAuthListener;
    private String mUserId;

    protected String mClientId;
    protected String mClientSecret;
    protected String mClientRedirectUrl;
    protected AuthenticationInfo mAuthInfo;

    /**
     * Optional refresh provider.
     */
    protected AuthenticationRefreshProvider mRefreshProvider;

    /**
     * When using this constructor, if a user has previously been logged in/stored or there is only one user, this user will be authenticated.
     * If no user or multiple users have been stored without knowledge of the last one authenticated, ui will be shown to handle the scenario similar
     * to OAuthSession(null, context).
     * @param context  current context.
     */
    public OAuthSession(Context context) {
        this(context, getBestStoredUserId(context));
    }

    /**
     *
     * @return the user id associated with the only logged in user. If no user is logged in or multiple users are logged in returns null.
     */
    private static String getBestStoredUserId(final Context context){
        String lastAuthenticatedUserId = AuthenticationManager.getInstance().getLastAuthenticatedUserId(context);
        Map<String, AuthenticationInfo> authInfoMap = AuthenticationManager.getInstance().getStoredAuthInfo(context);
        if(authInfoMap != null){
            if (!SdkUtils.isEmptyString(lastAuthenticatedUserId) && authInfoMap.get(lastAuthenticatedUserId) != null){
                return lastAuthenticatedUserId;
            }
            if (authInfoMap.size() == 1) {
                return authInfoMap.keySet().iterator().next();
            }
        }
        return null;
    }

    /**
     * When setting the userId to null ui will be shown to ask which user to authenticate as if at least one user is logged in. If no
     * user has been stored will show login ui. If logging in as a valid user id no ui will be displayed.
     * @param userId user id to login as or null to login a new user.
     * @param context current context.
     */
    public OAuthSession(Context context, String userId) {
        this(context, userId, AuthenticationManager.getInstance().getRefreshProvider()
        );
    }

    public OAuthSession(Context context, String userId, AuthenticationRefreshProvider authenticationRefreshProvider) {
        this(context, userId, SdkConfig.CLIENT_ID, SdkConfig.CLIENT_SECRET, SdkConfig.REDIRECT_URL);
        this.mRefreshProvider = authenticationRefreshProvider;
    }

    /**
     * Create a OAuthSession using a specific clientId, secret, and redirectUrl. This constructor is not necessary unless
     * an application uses multiple api keys.
     * Note: When setting the userId to null ui will be shown to ask which user to authenticate as if at least one user is logged in. If no
     * user has been stored will show login ui.
     * @param context current context.
     * @param userId user id to login as or null to login as a new user.
     */
    public OAuthSession(Context context, String userId, String clientId, String clientSecret, String redirectUrl) {
        mClientId = clientId;
        mClientSecret = clientSecret;
        mClientRedirectUrl = redirectUrl;
        if (SdkUtils.isEmptyString(mClientId) || SdkUtils.isEmptyString(mClientSecret)){
            throw new RuntimeException("Session must have a valid client id and client secret specified.");
        }
        mApplicationContext = context.getApplicationContext();
        if (!SdkUtils.isEmptyString(userId)) {
            mAuthInfo = AuthenticationManager.getInstance().getAuthInfo(userId, context);
            mUserId = userId;
        }
        if (mAuthInfo == null) {
            mUserId = userId;
            mAuthInfo = new AuthenticationInfo();
        }
        mAuthInfo.setClientId(mClientId);
        setupSession();
    }

    /**
     * Construct a new session object based off of an existing session.
     * @param session session to use as the base.
     */
    protected OAuthSession(OAuthSession session) {
        this.mApplicationContext = session.mApplicationContext;
        this.mAuthInfo = session.getAuthInfo();
        setupSession();
    }


    /**
     * This is an advanced constructor that can be used when implementing an authentication flow that differs from the default oauth 2 flow.
     * @param context current context.
     * @param authInfo authentication information that should be used. (Must at the minimum provide an access token).
     * @param refreshProvider the refresh provider to use when the access token expires and needs to be refreshed.
     */
    public OAuthSession(Context context, AuthenticationInfo authInfo, AuthenticationRefreshProvider refreshProvider) {
        mApplicationContext = context.getApplicationContext();
        mAuthInfo = authInfo;
        mRefreshProvider = refreshProvider;
        if (authInfo.getUserInfo() != null){
            if (!SdkUtils.isBlank(authInfo.getUserInfo().getId())){
                mUserId = authInfo.getUserInfo().getId();
            }
        }
    }

    private static AuthenticationInfo createSimpleAuthenticationInfo(final String accessToken){
        AuthenticationInfo info = new AuthenticationInfo();
        info.setAccessToken(accessToken);
        return info;
    }

    /**
     *
     * @return the application context used to construct this session.
     */
    public Context getApplicationContext() {
        return mApplicationContext;
    }

    /**
     *
     * @param listener listener to notify when authentication events (authentication, refreshing, and their exceptions) occur.
     */
    public void setSessionAuthListener(AuthenticationListener listener) {
        this.sessionAuthListener = listener;
    }

    protected void setupSession() {
        // Because BuildConfig.DEBUG is always false when library projects publish their release variants we use ApplicationInfo
        boolean isDebug = false;
        try {
            if (mApplicationContext != null && mApplicationContext.getPackageManager() != null) {
                PackageInfo info = mApplicationContext.getPackageManager().getPackageInfo(mApplicationContext.getPackageName(), 0);
                isDebug = ((info.applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0);
            }
        } catch (PackageManager.NameNotFoundException e) {
            // Do nothing -- debug mode will default to false
        }
        SdkConfig.IS_DEBUG = isDebug;
        AuthenticationManager.getInstance().addListener(this);
    }

    /**
     *
     * @return the user associated with this session. May return null if this is a new session before authentication.
     */
    public UserInfo getUserInfo() {
        return mAuthInfo.getUserInfo();
    }

    /**
     *
     * @return the user id associated with this session. This can be null if the session was created without a user id and has
     * not been authenticated.
     */
    public String getUserId(){
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public Role getRole() {
        UserInfo userInfo = getUserInfo();
        return userInfo == null ? null : userInfo.getRole();
    }

    /**
     *
     * @return the auth information associated with this session.
     */
    public AuthenticationInfo getAuthInfo() {
        return mAuthInfo;
    }

    /**
     *
     * @return the custom refresh provider associated with this session. returns null if one is not set.
     */
    public AuthenticationRefreshProvider getRefreshProvider(){
        return mRefreshProvider;
    }

    /**
     *
     * @return the user agent to use for network requests with this session.
     */
    public String getUserAgent() {
        return mUserAgent;
    }

    /**
     *
     * @return a future task (already submitted to an executor) that starts the process of authenticating this user.
     * The task can be used to block until the user has completed authentication through whatever ui is necessary(using task.get()).
     */
    public SdkFutureTask<OAuthSession> authenticate(RequestCompleteCallback<OAuthSession> listener) {
        SessionAuthCreationRequest req = new SessionAuthCreationRequest(this);
        return req.executeAsyncOnBackground(listener, AUTH_CREATION_EXECUTOR);
    }

    /**
     * Logout the currently authenticated user.
     * @return a task that can be used to block until the user associated with this session has been logged out.
     */
    public SdkFutureTask<OAuthSession> logout() {
//        final SdkFutureTask<OAuthSession> task = (new SessionLogoutRequest(this)).toTask();
//        new AsyncTask<Void, Void, Void> () {
//            @Override
//            protected Void doInBackground(Void... params) {
//                task.run();
//                return null;
//            }
//        }.execute();
//        return task;
        return (new SessionLogoutRequest(this)).executeAsyncOnBackground(null);
    }

    /**
     * Refresh authentication information associated with this session.
     * @return a task that can be used to block until the information associated with this session has been refreshed.
     */
    public SdkFutureTask<OAuthSession> refresh() {

//        final SdkFutureTask<OAuthSession> task = (new SessionRefreshRequest(this)).toTask();
//        new AsyncTask<Void, Void, Void> () {
//            @Override
//            protected Void doInBackground(Void... params) {
//                task.run();
//                return null;
//            }
//        }.execute();
//        return task;
        return (new SessionRefreshRequest(this)).executeAsyncOnBackground(null);
    }

    /**
     * Called when this session has been refreshed with new authentication info.
     * @param info the latest info from a successful refresh.
     */
    @Override
    public void onRefreshed(AuthenticationInfo info) {
        if (sameUser(info)) {
            AuthenticationInfo.cloneInfo(mAuthInfo, info);
            if (sessionAuthListener != null) {
                sessionAuthListener.onRefreshed(info);
            }
        }
    }

    /**
     * Called when this user has logged in.
     * @param info the latest info from going through the login flow.
     */
    @Override
    public void onAuthCreated(AuthenticationInfo info) {
        if (sameUser(info)) {
            AuthenticationInfo.cloneInfo(mAuthInfo, info);
            if (sessionAuthListener != null) {
                sessionAuthListener.onAuthCreated(info);
            }
        }
    }

    /**
     * Called when a failure occurs trying to authenticate or refresh.
     * @param info The last authentication information available, before the exception.
     * @param ex the exception that occurred.
     */
    @Override
    public void onAuthFailure(AuthenticationInfo info, Exception ex){
        if (sameUser(info) || (info == null && getUserId() == null)) {
            if (sessionAuthListener != null) {
                sessionAuthListener.onAuthFailure(info, ex);
            }
            if (ex instanceof SdkException){
                ErrorType errorType = ((SdkException) ex).getErrorType();
                switch(errorType){
                    case NETWORK_ERROR:
                        toastString(mApplicationContext, R.string.sdk_error_network_connection);
                }

            }
        }
    }

    private static AtomicLong mLastToastTime = new AtomicLong();
    private static void toastString(final Context context, final int id){
        Handler handler = new Handler(Looper.getMainLooper());
        long currentTime = System.currentTimeMillis();
        long lastToastTime = mLastToastTime.get();
        if (currentTime - 3000 < lastToastTime){
            return;
        }
        mLastToastTime.set(currentTime);
        handler.post(new Runnable(){
            @Override
            public void run() {
                Toast.makeText(context,id, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onLoggedOut(AuthenticationInfo info, Exception ex) {
        if (sameUser(info)) {
            if (ex instanceof AuthenticationManager.NonDefaultClientLogoutException) {
                logout();
            } else if (sessionAuthListener != null) {
                sessionAuthListener.onLoggedOut(info, ex);
            }
        }
    }

    /**
     * Returns the associated client id. If none is set, the one defined in SdkConfig will be used
     *
     * @return the client id this session is associated with.
     */
    public String getClientId() {
        return mClientId;
    }

    /**
     * Returns the associated client secret. Because client secrets are not managed by the SDK, if another
     * client id/secret is used aside from the one defined in the SdkConfig
     *
     * @return the client secret this session is associated with
     */
    public String getClientSecret() {
        return mClientSecret;
    }

    /**
     *
     * @return the redirect url this session is using. By default comes from SdkConstants.
     */
    public String getRedirectUrl() {
        return mClientRedirectUrl;
    }

    private boolean sameUser(AuthenticationInfo info) {
        return info != null && info.getUserInfo() != null && getUserId() != null && getUserId().equals(info.getUserInfo().getId());
    }

    private static class SessionLogoutRequest extends Request<OAuthSession> {
        final private OAuthSession mSession;

        public SessionLogoutRequest(OAuthSession session) {
            super(null, " ", null);
            this.mSession = session;
        }

        public OAuthSession execute() throws SdkException {
            synchronized (mSession) {
                if (mSession.getUserInfo() != null) {
                    AuthenticationManager.getInstance().logout(mSession);
                    mSession.getAuthInfo().wipeOutAuth();
                }
            }
            return mSession;
        }
    }


    private static class SessionRefreshRequest extends Request<OAuthSession> {
        private OAuthSession mSession;

        public SessionRefreshRequest(OAuthSession session) {
            super(null, " ", null);
            this.mSession = session;
        }

        public OAuthSession execute() throws SdkException {
            try {
                // block until this session is finished refreshing.
                AuthenticationInfo refreshedInfo = AuthenticationManager.getInstance().refresh(mSession).get();
            } catch (Exception e){
                if (e.getCause() instanceof SdkException){
                    throw (SdkException)e.getCause();
                } else {
                    throw new SdkException("OAuthSessionRefreshRequest failed", e);
                }
            }
            AuthenticationInfo.cloneInfo(mSession.mAuthInfo,
                    AuthenticationManager.getInstance().getAuthInfo(mSession.getUserId(), mSession.getApplicationContext()));
            return mSession;
        }
    }

    private static class SessionAuthCreationRequest extends Request<OAuthSession> implements AuthenticationListener {
        private final OAuthSession mSession;
        private CountDownLatch authLatch;

        public SessionAuthCreationRequest(OAuthSession session) {
            super(null, " ", null);
            this.mSession = session;
        }

        public OAuthSession execute() throws SdkException {
            synchronized (mSession) {
                if (mSession.getUserInfo() == null) {
                    if (mSession.getAuthInfo() != null && !SdkUtils.isBlank(mSession.getAuthInfo().getAccessToken())){

                        // if we have an access token, but no user try to repair by making the call to user endpoint.
                        try {
                            // TODO: show some ui while request user info
                            OAuthEndpoint oAuthEndpoint = new OAuthEndpoint((mSession));
                            UserInfo user = oAuthEndpoint.getUserInfo().execute();

                            mSession.setUserId(user.getId());
                            mSession.getAuthInfo().setUserInfo(user);
                            mSession.onAuthCreated(mSession.getAuthInfo());
                            return mSession;

                        } catch (SdkException e) {
                            LogUtils.e("OAuthSession", "Unable to repair user", e);
                            if (e.getErrorType() == ErrorType.INVALID_REFRESH_TOKEN || e.isErrorFatal()){
                                // if the refresh failure is unrecoverable have the user login again.
                                toastString(mSession.getApplicationContext(), R.string.sdk_error_fatal_refresh);
                            } else {
                                mSession.onAuthFailure(null, e);
                                throw e;
                            }
                        }
                        // at this point we were unable to repair.

                    }
                    AuthenticationManager.getInstance().addListener(this);
                    launchAuthUI();
                    return mSession;
                } else {
                        AuthenticationInfo info = AuthenticationManager.getInstance().getAuthInfo(mSession.getUserId(), mSession.getApplicationContext());
                        if (info != null) {
                            AuthenticationInfo.cloneInfo(mSession.mAuthInfo, info);
                            mSession.onAuthCreated(mSession.getAuthInfo());
                        } else {
                            // Fail to get information of current user. current use no longer valid.
                            mSession.mAuthInfo.setUserInfo(null);
                            launchAuthUI();
                        }
                }

                return mSession;
            }
        }

        public void launchAuthUI() {
            authLatch = new CountDownLatch(1);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (mSession.getRefreshProvider() != null && mSession.getRefreshProvider().launchAuthUi(mSession.getUserId(), mSession)) {
                        // Do nothing authentication ui will be handled by developer.
                    } else {
                        AuthenticationManager.getInstance().startAuthenticationUI(mSession);
                    }
                }
            });
            try {
                authLatch.await();
            } catch (InterruptedException e) {
                authLatch.countDown();
            }
        }

        @Override
        public void onRefreshed(AuthenticationInfo info) {
            // Do not implement, this class itself only handles auth creation, regardless success or not, failure should be handled by caller.
        }

        @Override
        public void onAuthCreated(AuthenticationInfo info) {
            AuthenticationInfo.cloneInfo(mSession.mAuthInfo, info);
            mSession.setUserId(info.getUserInfo().getId());
            mSession.onAuthCreated(info);
            authLatch.countDown();
        }

        @Override
        public void onAuthFailure(AuthenticationInfo info, Exception ex) {
            authLatch.countDown();
            // Do not implement, this class itself only handles auth creation, regardless success or not, failure should be handled by caller.
        }

        @Override
        public void onLoggedOut(AuthenticationInfo info, Exception ex) {
            // Do not implement, this class itself only handles auth creation, regardless success or not, failure should be handled by caller.
        }
    }
}
