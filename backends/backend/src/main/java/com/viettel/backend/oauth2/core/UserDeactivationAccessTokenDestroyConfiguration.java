package com.viettel.backend.oauth2.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;

import com.viettel.backend.service.common.UserDeactivationEvent;

import reactor.bus.EventBus;

/**
 * Listen for {@link UserDeactivationEvent} for remove related access tokens
 * 
 * @author thanh
 */
@Configuration
public class UserDeactivationAccessTokenDestroyConfiguration {
    
    @Autowired
    private EventBus eventBus;
    
    @Autowired
    private TokenStore tokenStore;
    
    @Autowired
    private OAuth2Properties oAuth2Properties;
    
    @Bean
    public TokenDestroyUserDeactivationListener tokenDestroyUserDeactivationListener() {
        return new TokenDestroyUserDeactivationListener(eventBus, tokenStore, oAuth2Properties.getClientId());
    }
    
}
