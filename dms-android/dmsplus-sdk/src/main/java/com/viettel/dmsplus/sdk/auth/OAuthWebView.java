package com.viettel.dmsplus.sdk.auth;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import com.viettel.dmsplus.sdk.SdkConfig;
import com.viettel.dmsplus.sdk.SdkConstants;
import com.viettel.dmsplus.sdk.R;
import com.viettel.dmsplus.sdk.utils.SdkUtils;

/**
 * A WebView used for OAuth flow.
 */
public class OAuthWebView extends WebView {

    /**
     * A state string query param set when loading the OAuth url. This will be validated in the redirect url.
     */
    private String state;

    /**
     * Constructor.
     * 
     * @param context
     *            context
     * @param attrs
     *            attrs
     */
    public OAuthWebView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * State string. This string is optionally appended to the OAuth url query param. If appended, it will be returned as query param in the redirect url too.
     * You can then verify that the two strings are the same as a security check.
     */
    public String getStateString() {
        return state;
    }

    /**
     * Start authentication.
     */
    public void authenticate(final String clientId, final String redirectUrl) {
        state = SdkUtils.generateStateToken();
        loadUrl(SdkConfig.getRequestAuthorizeUrl() +
                        "?" + SdkConstants.KEY_RESPONSE_TYPE + "=" + SdkConstants.RESPONSE_TYPE_CODE +
                        "&" + SdkConstants.KEY_CLIENT_ID + "=" + clientId +
                        "&" + SdkConstants.KEY_REDIRECT_URL + "=" + redirectUrl +
                        "&" + SdkConstants.KEY_STATE + "=" + state

        );
    }

    /**
     * WebViewClient for the OAuth WebView.
     */
    public static class OAuthWebViewClient extends WebViewClient {

        private boolean sslErrorDialogButtonClicked;

        private OAuthActivity mActivity;
        private String mRedirectUrl;
        private OnPageFinishedListener mOnPageFinishedListener;
        /**
         * a state string query param set when loading the OAuth url. This will be validated in the redirect url.
         */
        private String state;

        /**
         * Constructor.
         *
         * @param activity
         *            activity hosting this webview
         * @param  redirectUrl
         *            (optional) redirect url, for validation only.
         * @param stateString
         *            a state string query param set when loading the OAuth url. This will be validated in the redirect url.
         */
        public OAuthWebViewClient(OAuthActivity activity, String redirectUrl, String stateString) {
            super();
            this.mActivity = activity;
            this.mRedirectUrl = redirectUrl;
            this.state = stateString;
        }

        @Override
        public void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
            try {
                String code = getCodeFromUrl(url);

                if (!SdkUtils.isEmptyString(code)) {
                    mActivity.onReceivedAuthCode(code);
                }
            } catch (InvalidUrlException e) {
                mActivity.onAuthFailure(new AuthFailure(AuthFailure.TYPE_URL_MISMATCH, null));
            }
        }

        @Override
        public void onPageFinished(final WebView view, final String url) {
            super.onPageFinished(view, url);
            if (mOnPageFinishedListener != null) {
                mOnPageFinishedListener.onPageFinished(view, url);
            }
        }

        @Override
        public void onReceivedHttpAuthRequest(final WebView view, final HttpAuthHandler handler, final String host, final String realm) {
            LayoutInflater factory = mActivity.getLayoutInflater();
            final View textEntryView = factory.inflate(R.layout.sdk_alert_dialog_text_entry, null);

            AlertDialog loginAlert = new AlertDialog.Builder(mActivity).setTitle(R.string.sdk_alert_dialog_text_entry).setView(textEntryView)
                .setPositiveButton(R.string.sdk_alert_dialog_ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(final DialogInterface dialog, final int whichButton) {
                        String userName = ((EditText) textEntryView.findViewById(R.id.username_edit)).getText().toString();
                        String password = ((EditText) textEntryView.findViewById(R.id.password_edit)).getText().toString();
                        handler.proceed(userName, password);
                    }
                }).setNegativeButton(R.string.sdk_alert_dialog_cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(final DialogInterface dialog, final int whichButton) {
                        handler.cancel();
                        mActivity.onAuthFailure(new AuthFailure(AuthFailure.TYPE_USER_INTERACTION, null));
                    }
                }).create();
            loginAlert.show();
        }

        @Override
        public void onReceivedSslError(final WebView view, final SslErrorHandler handler, final SslError error) {

            Resources resources = view.getContext().getResources();
            StringBuilder sslErrorMessage = new StringBuilder(
                    resources.getString(R.string.sdk_There_are_problems_with_the_security_certificate_for_this_site));
            sslErrorMessage.append(" ");
            String sslErrorType;
            switch (error.getPrimaryError()) {
                case SslError.SSL_DATE_INVALID:
                    sslErrorType = view.getResources().getString(R.string.sdk_ssl_error_warning_DATE_INVALID);
                    break;
                case SslError.SSL_EXPIRED:
                    sslErrorType = resources.getString(R.string.sdk_ssl_error_warning_EXPIRED);
                    break;
                case SslError.SSL_IDMISMATCH:
                    sslErrorType = resources.getString(R.string.sdk_ssl_error_warning_ID_MISMATCH);
                    break;
                case SslError.SSL_NOTYETVALID:
                    sslErrorType = resources.getString(R.string.sdk_ssl_error_warning_NOT_YET_VALID);
                    break;
                case SslError.SSL_UNTRUSTED:
                    sslErrorType = resources.getString(R.string.sdk_ssl_error_warning_UNTRUSTED);
                    break;
                case SslError.SSL_INVALID:
                    sslErrorType = resources.getString(R.string.sdk_ssl_error_warning_INVALID);
                    break;
                default:
                    sslErrorType = resources.getString(R.string.sdk_ssl_error_warning_INVALID);
                    break;
            }
            sslErrorMessage.append(sslErrorType);
            sslErrorMessage.append(" ");
            sslErrorMessage.append(resources.getString(R.string.sdk_ssl_should_not_proceed));
            // Show the user a dialog to force them to accept or decline the SSL problem before continuing.
            sslErrorDialogButtonClicked = false;
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(view.getContext()).setTitle(R.string.sdk_Security_Warning)
                    .setMessage(sslErrorMessage.toString()).setIcon(R.drawable.sdk_dialog_warning)
                    .setNegativeButton(R.string.sdk_Go_back, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(final DialogInterface dialog, final int whichButton) {
                            sslErrorDialogButtonClicked = true;
                            handler.cancel();
                            mActivity.onAuthFailure(new AuthFailure(AuthFailure.TYPE_USER_INTERACTION, null));
                        }
                    });

            // Only allow user to continue if explicitly granted in config
            if (SdkConfig.ALLOW_SSL_ERROR) {
                alertBuilder.setPositiveButton(R.string.sdk_Continue, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int whichButton) {
                        sslErrorDialogButtonClicked = true;
                        handler.proceed();
                    }
                });
            }

            AlertDialog loginAlert = alertBuilder.create();
            loginAlert.setOnDismissListener(new OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (!sslErrorDialogButtonClicked) {
                        mActivity.onAuthFailure(new AuthFailure(AuthFailure.TYPE_USER_INTERACTION, null));
                    }
                }
            });
            loginAlert.show();
        }

        /**
         * Destroy.
         */
        public void destroy() {
            mActivity = null;
        }

        /**
         * Get response value.
         * 
         * @param url
         *            url
         * @return response value
         * @throws InvalidUrlException
         */
        private String getCodeFromUrl(final String url) throws InvalidUrlException {
            Uri uri = Uri.parse(url);
            // In case redirect url is set. We only keep processing if current url matches redirect url.
            if (!SdkUtils.isEmptyString(mRedirectUrl)) {
                Uri redirectUri = Uri.parse(mRedirectUrl);
                if (redirectUri.getScheme() == null || !redirectUri.getScheme().equals(uri.getScheme()) || !redirectUri.getAuthority().equals(uri.getAuthority())) {
                    return null;
                }
            }

            String code = null;

            try {
                code = uri.getQueryParameter(SdkConstants.KEY_CODE);
            } catch (Exception e) {
                // uri cannot be parsed for query param.
            }
            if (!SdkUtils.isEmptyString(code)) {
                // Check state token
                if (!SdkUtils.isEmptyString(state)) {
                    String stateQ = uri.getQueryParameter(SdkConstants.KEY_STATE);
                    if (!state.equals(stateQ)) {
                        throw new InvalidUrlException();
                    }

                }

            }
            return code;
        }

        public void setOnPageFinishedListener(OnPageFinishedListener listener) {
            this.mOnPageFinishedListener = listener;
        }
    }

    /**
     * Listener to listen to the event of a page load finishing.
     */
    public interface OnPageFinishedListener {

        void onPageFinished(final WebView view, final String url);
    }

    /**
     * Exception indicating url validation failed.
     */
    private static class InvalidUrlException extends Exception {

        private static final long serialVersionUID = 1L;
    }

    /**
     * Class containing information of an authentication failure.
     */
    public static class AuthFailure {

        public static final int TYPE_USER_INTERACTION = 0;
        public static final int TYPE_URL_MISMATCH = 1;

        public int type;
        public String message;

        public AuthFailure(int failType, String failMessage) {
            this.type = failType;
            this.message = failMessage;
        }
    }
}
