package com.viettel.dmsplus.sdk.auth;

import com.viettel.dmsplus.sdk.Endpoint;
import com.viettel.dmsplus.sdk.SdkConfig;
import com.viettel.dmsplus.sdk.SdkConstants;
import com.viettel.dmsplus.sdk.models.ChangePasswordDto;
import com.viettel.dmsplus.sdk.models.ErrorInfo;
import com.viettel.dmsplus.sdk.models.UserInfo;
import com.viettel.dmsplus.sdk.network.Request;

import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * @author Thanh
 */
public class OAuthEndpoint extends Endpoint {

    /**
     * Constructor.
     */
    public OAuthEndpoint(OAuthSession account) {
        super(account);
    }

    public Request<UserInfo> getUserInfo() {
        return get(SdkConfig.getUserInfoUrl(), UserInfo.class);
    }

    public Request<AuthenticationInfo> requestAccessToken(String username, String password,
        String clientId, String clientSecret) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>(5);
        body.set(SdkConstants.KEY_GRANT_TYPE, SdkConstants.GRANT_TYPE_PASSWORD);
        body.set(SdkConstants.KEY_CLIENT_ID, clientId);
        body.set(SdkConstants.KEY_CLIENT_SECRET, clientSecret);
        body.set(SdkConstants.KEY_USERNAME, username);
        body.set(SdkConstants.KEY_PASSWORD, password);
        return post(SdkConfig.getRequestTokenUrl(), body, AuthenticationInfo.class);
    }

    public Request<ErrorInfo> requestChangePassword(String oldPassword, String newPassword) {
        ChangePasswordDto dto = new ChangePasswordDto();
        dto.setOldPassword(oldPassword);
        dto.setNewPassword(newPassword);

        return put(SdkConfig.getChangePasswordUrl(), dto, ErrorInfo.class);
    }

    public Request<AuthenticationInfo> refreshAccessToken(String refreshToken, String clientId, String clientSecret) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>(4);
        body.set(SdkConstants.KEY_GRANT_TYPE, SdkConstants.GRANT_TYPE_REFRESH);
        body.set(SdkConstants.KEY_REFRESH_TOKEN, refreshToken);
        body.set(SdkConstants.KEY_CLIENT_ID, clientId);
        body.set(SdkConstants.KEY_CLIENT_SECRET, clientSecret);
        return post(SdkConfig.getRequestTokenUrl(), body, AuthenticationInfo.class);
    }

    Request<AuthenticationInfo> exchangeCodeForAccessToken(String code,
            String clientId, String clientSecret, String redirectUrl) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>(5);
        body.set(SdkConstants.KEY_GRANT_TYPE, SdkConstants.GRANT_TYPE_AUTH_CODE);
        body.set(SdkConstants.KEY_CODE, code);
        body.set(SdkConstants.KEY_CLIENT_ID, clientId);
        body.set(SdkConstants.KEY_CLIENT_SECRET, clientSecret);
        body.set(SdkConstants.KEY_REDIRECT_URL, redirectUrl);
        return post(SdkConfig.getRequestTokenUrl(), body, AuthenticationInfo.class);
    }

    Request<Void> revokeAccessToken(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(SdkConstants.HEADER_AUTHORIZATION, "Bearer " + token);
        return delete(SdkConfig.getRevokeTokenUrl(), Void.class, headers, token)
                .setSkipAuthError(true);
    }

}
