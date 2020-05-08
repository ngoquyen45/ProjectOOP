package com.viettel.backend.config.root;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Frontend configuration properties.
 * 
 * @author thanh
 */
@ConfigurationProperties("frontend")
public class FrontendProperties {

    /**
     * Url of web-frontend. This will become default redirect URL
     */
    private String webUrl;
    
    /**
     * Only allowed cross-origin access from following Origins. Default is empty, mean only allow access from
     * same domain. If domain of web app differ from domain of backend, set this value to url of web app
     */
    private List<String> allowedOrigins;

    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }
    
}
