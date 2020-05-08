package com.viettel.backend.oauth2.core;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * OAuth 2.0 configuration properties.
 * 
 * @author thanh
 */
@ConfigurationProperties("oauth2")
public class OAuth2Properties {

    private String realm;
    
    /**
     * Verifier key used to sign all download request
     */
    private String signedRequestVerifierKey;
    
    /**
     * OAuth 2 client_id
     */
    private String clientId;
    
    /**
     * OAuth 2 client_secret
     */
    private String clientSecret;
    
    /**
     * List of allowed grant type: implicit,refresh_token,password,authorization_code,client_credentials
     */
    private List<String> grantType;
    
    /**
     * Declare any scope you want, you can restrict access base on scope
     */
    private List<String> scope;
    
    /**
     * Refresh token life time in seconds
     */
    private int refreshTokenValidity;
    
    /**
     * Access token life time in seconds
     */
    private int accessTokenValidity;
    
    /**
     * Only allow following redirect urls
     */
    private List<String> redirectUri;

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public List<String> getGrantType() {
        return grantType;
    }

    public void setGrantType(List<String> grantType) {
        this.grantType = grantType;
    }

    public List<String> getScope() {
        return scope;
    }

    public void setScope(List<String> scope) {
        this.scope = scope;
    }

    public int getRefreshTokenValidity() {
        return refreshTokenValidity;
    }

    public void setRefreshTokenValidity(int refreshTokenValidity) {
        this.refreshTokenValidity = refreshTokenValidity;
    }

    public int getAccessTokenValidity() {
        return accessTokenValidity;
    }

    public void setAccessTokenValidity(int accessTokenValidity) {
        this.accessTokenValidity = accessTokenValidity;
    }

    public List<String> getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(List<String> redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getSignedRequestVerifierKey() {
        return signedRequestVerifierKey;
    }

    public void setSignedRequestVerifierKey(String signedRequestVerifierKey) {
        this.signedRequestVerifierKey = signedRequestVerifierKey;
    }
    
}
