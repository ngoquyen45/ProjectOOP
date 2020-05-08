package com.viettel.dmsplus.sdk.auth;

import com.viettel.dmsplus.sdk.SdkException;

/**
 * An interface that should be implemented if using a custom authentication scheme and not the default oauth 2 based token refresh logic.
 */
public interface AuthenticationRefreshProvider {

    /**
     * This method should return a refreshed authentication info object given one that is expired or nearly expired.
     * @param info the expired authentication information.
     * @return a refreshed AuthenticationInfo object. The object must include the valid access token.
     * @throws SdkException Exception that should be thrown if there was a problem fetching the information.
     */
    AuthenticationInfo refreshAuthenticationInfo(AuthenticationInfo info) throws SdkException;

    /**
     * This method should launch an activity or perform necessary logic in order to authenticate a user for the first time or re-authenticate a user if necessary.
     * Implementers should call AuthenticationManager.getInstance().onAuthenticated(AuthenticationInfo info, Context context) to complete authentication.
     * @param userId the user id that needs re-authentication if known. For a new user this will be null.
     * @param session the session that is attempting to launch authentication ui.
     * @return true if the ui is handled, if false the default authentication ui will be shown to authenticate the user.
     */
    boolean launchAuthUi(String userId, OAuthSession session);

}
