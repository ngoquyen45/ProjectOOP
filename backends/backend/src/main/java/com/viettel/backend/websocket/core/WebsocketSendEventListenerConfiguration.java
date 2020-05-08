package com.viettel.backend.websocket.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import reactor.bus.EventBus;

/**
 * Configuration class for {@link WebsocketSendEventListener}
 * 
 * @author thanh
 */
@Configuration
public class WebsocketSendEventListenerConfiguration {
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private EventBus eventBus;

    @Bean
    public WebsocketSendEventListener websocketSendEventListener() {
        return new WebsocketSendEventListener(messagingTemplate, eventBus);
    }
    
}
