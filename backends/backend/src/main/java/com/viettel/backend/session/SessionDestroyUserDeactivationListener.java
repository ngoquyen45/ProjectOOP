package com.viettel.backend.session;

import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;

import com.viettel.backend.service.common.UserDeactivationEvent;

import reactor.bus.EventBus;
import reactor.bus.selector.Selectors;
import reactor.fn.Consumer;

/**
 * Listen for {@link UserDeactivationEvent} and destroy all sessions associated with this user
 * 
 * @author thanh
 */
public class SessionDestroyUserDeactivationListener implements InitializingBean, Consumer<UserDeactivationEvent> {
    
    private EventBus eventBus;
    
    private UserSessionMappingRepository userSessionMappingRepository;
    
    private RedisOperationsSessionRepository redisOperationsSessionRepository;
    
    public SessionDestroyUserDeactivationListener(EventBus eventBus, 
            UserSessionMappingRepository userSessionMappingRepository, 
            RedisOperationsSessionRepository redisOperationsSessionRepository) {
        this.eventBus = eventBus;
        this.userSessionMappingRepository = userSessionMappingRepository;
        this.redisOperationsSessionRepository = redisOperationsSessionRepository;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        eventBus.on(Selectors.$(UserDeactivationEvent.EVENT_NAME), this);
    }

    @Override
    public void accept(UserDeactivationEvent event) {
        Set<String> sessionIds = userSessionMappingRepository.getSessionIds(event.getUser().getUsernameFull());
        for (String sessionId : sessionIds) {
            redisOperationsSessionRepository.delete(sessionId);
        }
    }

}
