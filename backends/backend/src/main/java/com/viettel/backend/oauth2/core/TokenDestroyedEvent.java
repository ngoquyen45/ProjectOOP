package com.viettel.backend.oauth2.core;

import org.springframework.context.ApplicationEvent;

/**
 * @author thanh
 */
public class TokenDestroyedEvent extends ApplicationEvent {
    
    private static final long serialVersionUID = 1437175994346049076L;
    
    private String token;
    
    public TokenDestroyedEvent(Object source, String token) {
        super(source);
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
