package com.viettel.backend.oauth2.core;

/**
 * @author thanh
 */
public class TicketResult {
    
    private String url;
    
    public TicketResult() {
        this(null);
    }
    
    public TicketResult(String url) {
        this.setUrl(url);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
