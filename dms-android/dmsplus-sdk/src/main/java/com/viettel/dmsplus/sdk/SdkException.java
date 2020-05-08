package com.viettel.dmsplus.sdk;

import com.viettel.dmsplus.sdk.auth.OAuthError;
import com.viettel.dmsplus.sdk.models.ErrorInfo;
import com.viettel.dmsplus.sdk.network.CustomHttpClientErrorException;
import com.viettel.dmsplus.sdk.network.CustomHttpServerErrorException;
import com.viettel.dmsplus.sdk.utils.JsonUtils;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.client.ResourceAccessException;

/**
 * Thrown to indicate that an error occurred while communicating with the server.
 */
public class SdkException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private ErrorInfo error;
    private OAuthError oauthError;
    private ErrorType errorType;

    /**
     * Constructs a SdkException with a specified message.
     *
     * @param message a message explaining why the exception occurred.
     */
    public SdkException(String message) {
        super(message);
        this.error = null;
        this.oauthError = null;
        this.errorType = ErrorType.OTHER;
    }

    public SdkException(String message, CustomHttpClientErrorException e) {
        super(message, e);

        String body = e.getResponseBodyAsString();
        OAuthError oauthError = JsonUtils.toObject(body, OAuthError.class);
        if (oauthError != null) {
            this.oauthError = oauthError;
            this.errorType = ErrorType.fromOAuthError(this.oauthError);
        } else {
            ErrorInfo.Envelope envelope = JsonUtils.toObject(body, ErrorInfo.Envelope.class);
            if (envelope != null && envelope.getMeta() != null) {
                this.error = envelope.getMeta();
                this.errorType = ErrorType.fromSdkError(this.error);
            } else {
                this.error = new ErrorInfo("unknown", e.getStatusCode().value(), e.getStatusCode().getReasonPhrase() + ": " + body);
                this.errorType = ErrorType.OTHER;
            }
        }
    }

    public SdkException(String message, CustomHttpServerErrorException e) {
        super(message, e);

        String body = e.getResponseBodyAsString();
        ErrorInfo error = JsonUtils.toObject(body, ErrorInfo.class);
        if (error != null) {
            this.error = error;
            this.errorType = ErrorType.fromSdkError(this.error);
        } else {
            this.error = new ErrorInfo("unknown", e.getStatusCode().value(), e.getStatusCode().getReasonPhrase() + ": " + body);
            this.errorType = ErrorType.OTHER;
        }
    }

    public SdkException(String message, Throwable cause) {
        super(message, cause);

        if (cause instanceof ResourceAccessException) {
            this.error = new ErrorInfo("ResourceAccessException", 601, cause.getMessage());
            this.errorType = ErrorType.NETWORK_ERROR;
        } else if (cause instanceof HttpMessageNotReadableException) {
            this.error = new ErrorInfo("HttpMessageNotReadableException", 602, cause.getMessage());
            this.errorType = ErrorType.OTHER;
        } else {
            this.error = new ErrorInfo("unknown", 600, cause.getMessage());
            this.errorType = ErrorType.OTHER;
        }
    }

    public SdkException(String message, ErrorInfo error, ErrorType type) {
        super(message, null);

        this.error = error;
        this.errorType = type;
    }

    public ErrorInfo getError() {
        return error;
    }

    public void setError(ErrorInfo error) {
        this.error = error;
    }

    public OAuthError getOAuthError() {
        return oauthError;
    }

    public void setOAuthError(OAuthError oauthError) {
        this.oauthError = oauthError;
    }

    public boolean isOAuthError() {
        return this.oauthError != null;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(ErrorType errorType) {
        this.errorType = errorType;
    }

    public boolean isErrorFatal() {
        ErrorType type = getErrorType();
        ErrorType[] fatalTypes = new ErrorType[] {
                ErrorType.INVALID_REFRESH_TOKEN,
                ErrorType.INVALID_CLIENT_CREDENTIALS,
                ErrorType.OAUTH_ERROR
        };
        for (ErrorType fatalType : fatalTypes) {
            if (type == fatalType) {
                return true;
            }
        }
        return false;
    }

}
