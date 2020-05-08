package com.viettel.backend.websocket.core;

import org.springframework.web.socket.WebSocketSession;

/**
 * @author thanh
 */
public interface WebSocketAuthenticationHandler {
    
    public void handleWebSocketSessionAuthentication(WebSocketSession session, String login, String passcode);
    
    public void handleWebSocketSessionClosed(WebSocketSession session);
    
}
