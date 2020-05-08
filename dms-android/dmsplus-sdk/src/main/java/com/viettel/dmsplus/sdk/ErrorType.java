package com.viettel.dmsplus.sdk;

import com.viettel.dmsplus.sdk.auth.OAuthError;
import com.viettel.dmsplus.sdk.models.ErrorInfo;

import org.springframework.http.HttpStatus;

/**
 * @author Thanh
 */
public enum ErrorType {
    /*
     * Invalid user credentials
     */
    INVALID_USER_CREDENTIALS,
    /*
     * Invalid refresh token
     */
    INVALID_REFRESH_TOKEN,
    /*
     * Invalid refresh token
     */
    INVALID_ACCESS_TOKEN,
    /*
     * The client credentials are invalid
     */
    INVALID_CLIENT_CREDENTIALS,
    /*
     * Any other OAuth error
     */
    OAUTH_ERROR,
    /*
     * Request params or method are not valid
     */
    BUSINESS_ERROR,
    /*
     * Request params or method are not valid
     */
    INVALID_REQUEST,
    /*
     * Internal server error
     */
    INTERNAL_SERVER_ERROR,
    /**
     * Could not connect to server due to a network error
     */
    NETWORK_ERROR,
    /**
     * An unknown exception has occurred.
     */
    OTHER;

    public static ErrorType fromOAuthError(OAuthError error) {
        if ("invalid_grant".equals(error.getError())) {
            if (error.getDescription().contains("Invalid refresh token")) {
                return INVALID_REFRESH_TOKEN;
            } else if (error.getDescription().contains("Bad User Credentials")) {
                return INVALID_USER_CREDENTIALS;
            }
        } else if ("invalid_client".equals(error.getError())) {
            if (error.getDescription().contains("Bad client credentials")) {
                return INVALID_CLIENT_CREDENTIALS;
            }
        } else if ("invalid_token".equals(error.getError())) {
            if (error.getDescription().contains("Invalid access token")) {
                return INVALID_ACCESS_TOKEN;
            }
        }
        return OAUTH_ERROR;
    }

    public static ErrorType fromSdkError(ErrorInfo error) {
        if ("MethodNotSupportedException".equals(error.getType())
                || "IllegalArgumentException".equals(error.getType())
                || "MediaTypeNotSupportedException".equals(error.getType())
                ) {
            return INVALID_REQUEST;
        }
        if ("BusinessException".equals(error.getType())) {
            return BUSINESS_ERROR;
        }
        if (error.getCode() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            return INTERNAL_SERVER_ERROR;
        }
        return OTHER;
    }
}