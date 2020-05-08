package com.viettel.backend.config.root;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Application properties.
 * 
 * @author trungkh
 */
@ConfigurationProperties("app")
public class AppProperties {

    private List<String> languages;

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }
    
}
