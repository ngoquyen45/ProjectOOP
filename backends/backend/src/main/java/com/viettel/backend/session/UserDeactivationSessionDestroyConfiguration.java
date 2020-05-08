package com.viettel.backend.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;

import reactor.bus.EventBus;

/**
 * Listen for {@link UserDeactivationEvent} for invalidate related sessions
 * 
 * @author thanh
 */
@Configuration
public class UserDeactivationSessionDestroyConfiguration {
    
    @Autowired
    private RedisOperationsSessionRepository redisOperationsSessionRepository;
    
    @Autowired
    private RedisConnectionFactory connectionFactory;
    
    @Autowired
    private EventBus eventBus;
    
    @Bean
    public UserSessionMappingRepository userSessionMappingRepository() {
        return new RedisUserSessionMappingRepository(connectionFactory);
    }
    
    @Bean
    public AuthenticationSuccessListener authenticationSuccessListener() {
        return new AuthenticationSuccessListener(userSessionMappingRepository());
    }
    
    @Bean
    public SessionDestroyedListener sessionDestroyedListener() {
        return new SessionDestroyedListener(userSessionMappingRepository());
    }
    
    @Bean
    public SessionDestroyUserDeactivationListener sessionDestroyUserDeactivationListener() {
        return new SessionDestroyUserDeactivationListener(eventBus, userSessionMappingRepository(), 
                redisOperationsSessionRepository);
    }
    
}
