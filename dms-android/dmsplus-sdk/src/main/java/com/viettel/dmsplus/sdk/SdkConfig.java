package com.viettel.dmsplus.sdk;

import com.viettel.dmsplus.sdk.auth.OAuthSession;

public class SdkConfig {

    /**
     * Flag for whether logging is enabled. This will log all requests and responses made by the SDK
     */
    public static boolean IS_LOG_ENABLED = true;

    /**
     * Flag for whether a user should be allowed to continue when there is an SSL error in the webview. Disabled by default.
     */
    public static boolean ALLOW_SSL_ERROR = false;

    /**
     * Because BuildConfig.DEBUG is always false when library projects publish their release variants we use ApplicationInfo
     * Flag for whether the app is currently run in debug mode. This is set by the {@link OAuthSession}
     * object and is determined from the {@link android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE}
     */
    public static boolean IS_DEBUG = true;

    /**
     * Client id used for the OAuth flow
     */
    public static String CLIENT_ID = "dmsplus";

    /**
     * Client secret used for the OAuth flow
     */
    public static String CLIENT_SECRET = "secret";

    /**
     * The redirect url used with OAuth flow
     */
    public static String REDIRECT_URL = "oauth2://dmsplus.sdk";

    public static String BASE_URL = "http://dmsplus.net:8080";
//public static String BASE_URL = "http://192.168.99.184:8080";
    /*public static String BASE_URL = "http://cloud.dmsone.vn:6969";
*/
    public static String getApiBaseUrl() {
        return BASE_URL + "/api";
    }

    public static String getRequestTokenUrl() {
        return BASE_URL + "/oauth/token";
    }

    public static String getRequestAuthorizeUrl() {
        return BASE_URL + "/oauth/authorize";
    }

    public static String getUserInfoUrl() {
        return BASE_URL + "/oauth/userinfo";
    }

    public static String getRevokeTokenUrl() {
        return BASE_URL + "/oauth/revoke/{token}";
    }

    public static String getChangePasswordUrl() {
        return BASE_URL + "/oauth/password";
    }
}

