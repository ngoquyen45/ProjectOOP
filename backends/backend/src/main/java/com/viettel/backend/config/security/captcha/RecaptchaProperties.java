package com.viettel.backend.config.security.captcha;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * reCAPTCHA configuration properties.
 * Extended version of {@link com.github.mkopylec.recaptcha.RecaptchaProperties}
 * 
 * @author thanh
 */
@ConfigurationProperties("recaptcha")
public class RecaptchaProperties extends com.github.mkopylec.recaptcha.RecaptchaProperties {

    /**
     * Properties responsible for reCAPTCHA validation on Google's servers.
     */
    private Validation validation = new Validation();
    /**
     * Properties responsible for integration with Spring Security.
     */
    private Security security = new Security();
    /**
     * Properties responsible for testing mode behaviour.
     */
    private Testing testing = new Testing();

    @Override
    public Validation getValidation() {
        return validation;
    }

    public void setValidation(Validation validation) {
        this.validation = validation;
    }

    @Override
    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    @Override
    public Testing getTesting() {
        return testing;
    }

    public void setTesting(Testing testing) {
        this.testing = testing;
    }
    
    /**
     * Extended version of {@link com.github.mkopylec.recaptcha.RecaptchaProperties.Validation}
     * 
     * @author thanh
     */
    public static class Validation extends com.github.mkopylec.recaptcha.RecaptchaProperties.Validation {
        
        /**
         * Site key for Google reCAPTCHA service
         */
        private String siteKey;

        public String getSiteKey() {
            return siteKey;
        }

        public void setSiteKey(String siteKey) {
            this.siteKey = siteKey;
        }
    }

    /**
     * Extended version of {@link com.github.mkopylec.recaptcha.RecaptchaProperties.Security}
     * 
     * @author thanh
     */
    public static class Security extends com.github.mkopylec.recaptcha.RecaptchaProperties.Security {
        
        /**
         * Enable or disable CAPTCHA feature
         */
        private boolean enable;
        
        /**
         * If set to 'true', every time user login, they have to resolve the CAPTCHA. Other wise, CAPTCHA only show when they login fail several time.
         */
        private boolean strictMode;
        
        /**
         * Max number of login fail, after that, user must have to resolve CAPTCHA. Only have effect if 'recaptcha.security.strictMode'='false'
         */
        private int loginFailCount = 3;

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public boolean isStrictMode() {
            return strictMode;
        }

        public void setStrictMode(boolean strictMode) {
            this.strictMode = strictMode;
        }

        public int getLoginFailCount() {
            return loginFailCount;
        }

        public void setLoginFailCount(int loginFailCount) {
            this.loginFailCount = loginFailCount;
        }
    }

    /**
     * Extended version of {@link com.github.mkopylec.recaptcha.RecaptchaProperties.Testing}
     * 
     * @author thanh
     */
    public static class Testing extends com.github.mkopylec.recaptcha.RecaptchaProperties.Testing {

    }
    
}
