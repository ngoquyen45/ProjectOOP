package com.viettel.backend.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * TOTP configuration properties.
 * 
 * @author thanh
 */
@ConfigurationProperties("totp")
public class TotpProperties {

    /**
     * Value indicating that TOTP shoud be enable or not
     */
    private boolean enable = false;
    
    /**
     * A base32 value of secret using for generate TOTP token
     */
    private String secret;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
    
}
