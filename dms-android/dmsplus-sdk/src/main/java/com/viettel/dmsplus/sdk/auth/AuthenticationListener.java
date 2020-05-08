package com.viettel.dmsplus.sdk.auth;

/**
 * Interface of a listener to listen to authentication events.
 */
public interface AuthenticationListener {

    void onRefreshed(AuthenticationInfo info);

    void onAuthCreated(AuthenticationInfo info);

    void onAuthFailure(AuthenticationInfo info, Exception ex);

    void onLoggedOut(AuthenticationInfo info, Exception ex);
}
