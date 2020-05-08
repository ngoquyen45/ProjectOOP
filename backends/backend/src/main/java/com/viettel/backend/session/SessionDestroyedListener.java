package com.viettel.backend.session;

import org.springframework.context.ApplicationListener;
import org.springframework.session.events.SessionDestroyedEvent;
import org.springframework.util.Assert;

/**
 * Listener for {@link SessionDestroyedEvent} for unregister mapping
 * @author thanh
 */
public class SessionDestroyedListener implements ApplicationListener<SessionDestroyedEvent> {
    
    private UserSessionMappingRepository userSessionMappingRepository;
    
    /**
     * Creates a new instance
     *
     * @param userSessionMappingRepository Cannot be null.
     */
    public SessionDestroyedListener(UserSessionMappingRepository userSessionMappingRepository) {
        Assert.notNull(userSessionMappingRepository, "userSessionMappingRepository cannot be null");
        this.userSessionMappingRepository = userSessionMappingRepository;
    }

    @Override
    public void onApplicationEvent(SessionDestroyedEvent event) {
        userSessionMappingRepository.unregisterSessionId(event.getSessionId());
    }
    
}
