package com.viettel.backend.websocket.core;

import java.io.Serializable;
import java.security.Principal;
import java.util.Map;

import org.springframework.util.StringUtils;

/**
 * @author thanh
 */
public class OAuth2TokenUserPrincipal implements Principal, Serializable {

    private static final long serialVersionUID = -3808357739655345323L;
    
    private String username;
    private Map<String, Object> attributes;

    public OAuth2TokenUserPrincipal(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getName() {
        if (StringUtils.isEmpty(username)) {
            if (attributes.containsKey(StompLoginSubProtocolWebSocketHandler.SESSION_KEY_VERYFIED)
                    && attributes.containsKey(StompLoginSubProtocolWebSocketHandler.SESSION_KEY_USERNAME)) {
                this.username = (String) attributes.get(StompLoginSubProtocolWebSocketHandler.SESSION_KEY_USERNAME);
            }
        }
        return this.username;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OAuth2TokenUserPrincipal other = (OAuth2TokenUserPrincipal) obj;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return true;
    }

}
