package com.viettel.backend.config.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.user.MultiServerUserRegistry;
import org.springframework.messaging.simp.user.UserSessionRegistry;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

import com.viettel.backend.config.root.FrontendProperties;
import com.viettel.backend.websocket.core.EnableWebsocketSendEvent;
import com.viettel.backend.websocket.core.OAuth2TokenHandshakeHandler;
import com.viettel.backend.websocket.core.OAuth2TokenWebSocketAuthenticationHandler;
import com.viettel.backend.websocket.core.RedisUserSessionRegistry;
import com.viettel.backend.websocket.core.StompLoginSubProtocolWebSocketHandlerDecoratorFactory;
import com.viettel.backend.websocket.core.WebSocketAuthenticationHandler;

/**
 * TODO: Considering using {@link MultiServerUserRegistry}
 * 
 * @author thanh
 */
@Configuration
@EnableWebSocketMessageBroker
@EnableWebsocketSendEvent
@EnableConfigurationProperties(StompProperties.class)
@SuppressWarnings("deprecation")
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Autowired
    private TokenStore tokenStore;
    
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;
    
    @Autowired
    private StompProperties stompProperties;
    
    @Autowired
    private FrontendProperties frontendProperties;
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // If STOMP broker not configured, create an simple fallback
        if (!stompProperties.isEmbedded()) {
            config.enableStompBrokerRelay("/topic", "/queue")
                    .setRelayHost(stompProperties.getHost())
                    .setRelayPort(stompProperties.getPort())
                    ;
        } else {
            config.enableSimpleBroker("/topic", "/queue");
        }
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
    	StompWebSocketEndpointRegistration endpoint = registry.addEndpoint("/websocket");
    	// If no allowed origin, mean only allow from same domain
    	if (!CollectionUtils.isEmpty(frontendProperties.getAllowedOrigins())) {
        	String[] arrAllowedOrigins = new String[frontendProperties.getAllowedOrigins().size()];
        	arrAllowedOrigins = frontendProperties.getAllowedOrigins().toArray(arrAllowedOrigins);
    		endpoint.setAllowedOrigins(arrAllowedOrigins);
    	}
        endpoint.setHandshakeHandler(new OAuth2TokenHandshakeHandler());
    	endpoint.withSockJS();
    }
    
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.addDecoratorFactory(loginSubProtocolWebSocketHandlerDecoratorFactory());
    }
    
    @Bean
    public StompLoginSubProtocolWebSocketHandlerDecoratorFactory loginSubProtocolWebSocketHandlerDecoratorFactory() {
        return new StompLoginSubProtocolWebSocketHandlerDecoratorFactory(webSocketAuthenticationProvider());
    }
    
    @Bean
    public WebSocketAuthenticationHandler webSocketAuthenticationProvider() {
        return new OAuth2TokenWebSocketAuthenticationHandler(tokenStore);
    }
    
    @Bean
    public UserSessionRegistry userSessionRegistry() {
        return new RedisUserSessionRegistry(redisConnectionFactory);
    }
    
}
