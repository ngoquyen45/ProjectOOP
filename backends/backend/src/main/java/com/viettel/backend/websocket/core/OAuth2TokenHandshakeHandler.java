package com.viettel.backend.websocket.core;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

/**
 * @author thanh
 */
public class OAuth2TokenHandshakeHandler extends DefaultHandshakeHandler {
    
    public OAuth2TokenHandshakeHandler() {
        super();
    }

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
            Map<String, Object> attributes) {
        return new OAuth2TokenUserPrincipal(attributes);
    }
}