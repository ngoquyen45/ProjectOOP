package com.viettel.backend.websocket.core;

import org.springframework.util.Assert;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

/**
 * @author thanh
 */
public class StompLoginSubProtocolWebSocketHandlerDecoratorFactory implements WebSocketHandlerDecoratorFactory {
    
    private WebSocketAuthenticationHandler webSocketAuthenticationProvider;
    
    public StompLoginSubProtocolWebSocketHandlerDecoratorFactory(WebSocketAuthenticationHandler webSocketAuthenticationProvider) {
        Assert.notNull(webSocketAuthenticationProvider, "WebSocketAuthenticationProvider cannot be null");
        this.webSocketAuthenticationProvider = webSocketAuthenticationProvider;
    }

    @Override
    public WebSocketHandler decorate(WebSocketHandler handler) {
        return new StompLoginSubProtocolWebSocketHandler(webSocketAuthenticationProvider, handler);
    }

    public WebSocketAuthenticationHandler getWebSocketAuthenticationProvider() {
        return webSocketAuthenticationProvider;
    }

    public void setWebSocketAuthenticationProvider(WebSocketAuthenticationHandler webSocketAuthenticationProvider) {
        this.webSocketAuthenticationProvider = webSocketAuthenticationProvider;
    }

}
