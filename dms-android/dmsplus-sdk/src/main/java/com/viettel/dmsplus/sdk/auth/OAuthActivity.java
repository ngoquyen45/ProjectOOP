package com.viettel.dmsplus.sdk.auth;

import java.io.File;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import com.viettel.dmsplus.sdk.SdkConstants;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.UserInfo;
import com.viettel.dmsplus.sdk.network.RequestCompleteCallback;
import com.viettel.dmsplus.sdk.R;
import com.viettel.dmsplus.sdk.auth.OAuthWebView.AuthFailure;
import com.viettel.dmsplus.sdk.auth.OAuthWebView.OAuthWebViewClient;
import com.viettel.dmsplus.sdk.utils.SdkUtils;

/**
 * Activity for OAuth. Use this activity by using the intent from createOAuthActivityIntent method. On completion, this activity will put the parcelable
 * authentication into the activity result. In the case of failure, the activity result will be {@link android.app.Activity#RESULT_CANCELED} together will a error message in
 * the intent extra.
 */
public class OAuthActivity extends AppCompatActivity implements ChooseAuthenticationFragment.OnAuthenticationChosen {

    /**
     * An optional boolean that can be set when creating the intent to launch this activity. If set to true it
     * will go directly to login flow, otherwise UI will be shown to let the user choose an already authenticated account first.
     */
    public static final String EXTRA_DISABLE_ACCOUNT_CHOOSING = "disableAccountChoosing";

    public static final String AUTH_INFO = "authinfo";

    private static final String CHOOSE_AUTH_TAG = "choose_auth";

    private String mClientId;
    private String mClientSecret;
    private String mRedirectUrl;
    protected OAuthWebView oauthView;
    protected OAuthWebViewClient oauthClient;
    private static Dialog dialog;
    private boolean mAuthWasSuccessful = false;

    private AtomicBoolean apiCallStarted = new AtomicBoolean(false);

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());

        Intent intent = getIntent();
        mClientId = intent.getStringExtra(SdkConstants.KEY_CLIENT_ID);
        mClientSecret = intent.getStringExtra(SdkConstants.KEY_CLIENT_SECRET);
        mRedirectUrl = intent.getStringExtra(SdkConstants.KEY_REDIRECT_URL);
        apiCallStarted.getAndSet(false);
        startOAuth();
    }

    /**
     * Callback method to be called when authentication code is received. The code will then be used to make an API call to create OAuth tokens.
     */
    public void onReceivedAuthCode(String code) {
        oauthView.setVisibility(View.INVISIBLE);
        startMakingOAuthAPICall(code);
    }

    @Override
    public void finish() {
        clearCachedAuthenticationData();
        if (!mAuthWasSuccessful) {
            AuthenticationManager.getInstance().onAuthenticationFailure(null, null);
        }
        super.finish();
    }

    /**
     * Callback method to be called when authentication failed.
     */
    public void onAuthFailure(AuthFailure failure) {
        if (SdkUtils.isEmptyString(failure.message)) {
            Toast.makeText(this, R.string.sdk_Authentication_fail, Toast.LENGTH_LONG).show();
        } else {
            switch (failure.type) {
                case AuthFailure.TYPE_URL_MISMATCH:
                    Resources resources = this.getResources();
                    Toast.makeText(
                            this,
                            String.format("%s\n%s: %s", resources.getString(R.string.sdk_Authentication_fail), resources.getString(R.string.sdk_details),
                                    resources.getString(R.string.sdk_Authentication_fail_url_mismatch)), Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(this, R.string.sdk_Authentication_fail, Toast.LENGTH_LONG).show();
            }
        }
        finish();
    }

    protected int getContentView() {
        return R.layout.sdk_activity_oauth;
    }

    protected void startOAuth() {
        // Use already logged in accounts if not disabled in this activity and not already showing this fragment.
        if (!getIntent().getBooleanExtra(EXTRA_DISABLE_ACCOUNT_CHOOSING, false) && getSupportFragmentManager().findFragmentByTag(CHOOSE_AUTH_TAG) == null){
            Map<String, AuthenticationInfo> map = AuthenticationManager.getInstance().getStoredAuthInfo(this);
            if (map != null && map.size() > 0) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.oauth_container, ChooseAuthenticationFragment.createAuthenticationActivity(this), CHOOSE_AUTH_TAG);
                transaction.addToBackStack(CHOOSE_AUTH_TAG);
                transaction.commit();
            }
        }
        this.oauthView = createOAuthView();
        this.oauthClient = createOAuthWebViewClient(oauthView.getStateString());
        oauthView.setWebViewClient(oauthClient);
        oauthView.authenticate(mClientId, mRedirectUrl);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentByTag(CHOOSE_AUTH_TAG) != null){
            dismissSpinnerAndFailAuthenticate(getString(R.string.sdk_Authentication_cancelled));
        }
        super.onBackPressed();
    }

    @Override
    public void onAuthenticationChosen(AuthenticationInfo authInfo) {
        if (authInfo != null){
            AuthenticationManager.getInstance().onAuthenticated(authInfo, OAuthActivity.this);
            dismissSpinnerAndFinishAuthenticate(authInfo);

        }
    }

    @Override
    public void onDifferentAuthenticationChosen() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(CHOOSE_AUTH_TAG);
        if (fragment != null){
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED){
            onAuthFailure(new AuthFailure(AuthFailure.TYPE_USER_INTERACTION, ""));
        }
    }

    /**
     * Start to create OAuth after getting the code.
     * 
     * @param code OAuth 2 authorization code
     */
    protected void startMakingOAuthAPICall(final String code) {
        if (apiCallStarted.getAndSet(true)) {
            return;
        }

        showSpinner();
        final OAuthSession session = new OAuthSession(this, null, mClientId, mClientSecret, mRedirectUrl);
        OAuthEndpoint api = new OAuthEndpoint(session);
        api.exchangeCodeForAccessToken(code, mClientId, mClientSecret, mRedirectUrl).executeAsyncOnBackground(new RequestCompleteCallback<AuthenticationInfo>() {
            @Override
            public void onSuccess(AuthenticationInfo auth) {
                AuthenticationInfo sessionAuth = session.getAuthInfo();
                sessionAuth.setAccessToken(auth.getAccessToken());
                sessionAuth.setRefreshToken(auth.getRefreshToken());
                sessionAuth.setRefreshTime(System.currentTimeMillis());
                sessionAuth.setClientId(session.getClientId());
                OAuthEndpoint oAuthEndpoint = new OAuthEndpoint(session);
                boolean fail = true;
                Exception exception = null;
                try {
                    UserInfo user = oAuthEndpoint.getUserInfo().execute();
                    sessionAuth.setUserInfo(user);
                    session.setUserId(user.getId());
                    AuthenticationManager.getInstance().onAuthenticated(sessionAuth, OAuthActivity.this);
                    fail = false;
                } catch (Exception e) {
                    exception = e;
                } finally {
                    if (!fail) {
                        dismissSpinnerAndFinishAuthenticate(sessionAuth);
                    } else {
                        dismissSpinnerAndFailAuthenticate(getAuthCreationErrorString(exception));
                    }
                }
            }

            @Override
            public void onError(SdkException ex) {
                dismissSpinnerAndFailAuthenticate(getAuthCreationErrorString(ex));
            }
        }, AuthenticationManager.AUTH_EXECUTOR);

    }

    protected void dismissSpinnerAndFinishAuthenticate(final AuthenticationInfo auth) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                dismissSpinner();
                Intent intent = new Intent();
                intent.putExtra(AUTH_INFO, auth);
                setResult(Activity.RESULT_OK, intent);
                mAuthWasSuccessful = true;
                finish();
            }

        });
    }

    protected void dismissSpinnerAndFailAuthenticate(final String error) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                dismissSpinner();
                Toast.makeText(OAuthActivity.this, error, Toast.LENGTH_LONG).show();
                setResult(Activity.RESULT_CANCELED);
                finish();
            }

        });
    }

    //@SuppressLint("SetJavaScriptEnabled")
    protected OAuthWebView createOAuthView() {
        OAuthWebView webview = (OAuthWebView) findViewById(getOAuthWebViewRId());
        webview.setVisibility(View.VISIBLE);
        //webview.getSettings().setJavaScriptEnabled(true);
        return webview;
    }

    protected OAuthWebViewClient createOAuthWebViewClient(String optionalState) {
        return new OAuthWebViewClient(this, mRedirectUrl, optionalState);
    }

    protected int getOAuthWebViewRId() {
        return R.id.oauthview;
    }

    /**
     * If you don't need the dialog, just return null.
     */
    protected Dialog showDialogWhileWaitingForAuthenticationAPICall() {
        return ProgressDialog.show(this, getText(R.string.sdk_Authenticating), getText(R.string.sdk_Please_wait));
    }

    protected void showSpinner() {
        try {
            dialog = showDialogWhileWaitingForAuthenticationAPICall();
        } catch (Exception e) {
            // WindowManager$BadTokenException will be caught and the app would not display
            // the 'Force Close' message
            dialog = null;
        }
    }

    protected void dismissSpinner() {
        if (dialog != null && dialog.isShowing()) {
            try {
                dialog.dismiss();
            } catch (IllegalArgumentException e) {
                // In certain case dialog already disattached from window.
            }
            dialog = null;
        }
    }

    @Override
    public void onDestroy() {
        apiCallStarted.set(false);
        dismissSpinner();
        super.onDestroy();
    }

    /**
     * Create intent to launch OAuthActivity.
     * 
     * @return  intent to launch OAuthActivity.
     */
    public static Intent createOAuthActivityIntent(final Context context, final String clientId, final String clientSecret, String redirectUrl) {
        Intent intent = new Intent(context, OAuthActivity.class);
        intent.putExtra(SdkConstants.KEY_CLIENT_ID, clientId);
        intent.putExtra(SdkConstants.KEY_CLIENT_SECRET, clientSecret);
        if (!SdkUtils.isEmptyString(redirectUrl)) {
            intent.putExtra(SdkConstants.KEY_REDIRECT_URL, redirectUrl);
        }
        return intent;
    }

    /**
     * Create intent to launch OAuthActivity using information from the given session.
     * @param context context
     * @param session the OAuthSession to use to get parameters required to authenticate via this activity.
     * @return intent to launch OAuthActivity.
     */
    public static Intent createOAuthActivityIntent(final Context context, OAuthSession session){
        return createOAuthActivityIntent(context, session.getClientId(), session.getClientSecret(), session.getRedirectUrl());
    }

    private String getAuthCreationErrorString(Exception e) {
        String error = OAuthActivity.this.getString(R.string.sdk_Authentication_fail);
        if (e != null) {
            error += ":" + e;
        }
        return error;
    }


    @SuppressWarnings("deprecation")
    private void clearCachedAuthenticationData() {
        if (oauthView != null) {
            oauthView.clearCache(true);
            oauthView.clearFormData();
            oauthView.clearHistory();
        }
        // wipe out cookies.
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();

        deleteDatabase("webview.db");
        deleteDatabase("webviewCache.db");
        File cacheDirectory = getCacheDir();
        SdkUtils.deleteFolderRecursive(cacheDirectory);
        cacheDirectory.mkdir();
    }
}
