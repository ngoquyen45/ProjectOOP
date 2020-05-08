package com.viettel.backend.session;

import org.springframework.context.ApplicationListener;
import org.springframework.security.web.authentication.session.SessionFixationProtectionEvent;
import org.springframework.util.Assert;

/**
 * Listener for {@link SessionFixationProtectionEvent} for register mapping
 * @author thanh
 */
public class AuthenticationSuccessListener implements ApplicationListener<SessionFixationProtectionEvent> {
    
    private UserSessionMappingRepository userSessionMappingRepository;
    
    /**
     * Creates a new instance
     *
     * @param userSessionMappingRepository Cannot be null.
     */
    public AuthenticationSuccessListener(UserSessionMappingRepository userSessionMappingRepository) {
        Assert.notNull(userSessionMappingRepository, "userSessionMappingRepository cannot be null");
        this.userSessionMappingRepository = userSessionMappingRepository;
    }

    @Override
    public void onApplicationEvent(SessionFixationProtectionEvent event) {
        if (event.getAuthentication() == null || event.getAuthentication().getPrincipal() == null) {
            return;
        }
        userSessionMappingRepository.registerSessionId(event.getNewSessionId(), 
                (String)event.getAuthentication().getPrincipal());
    }
    
}
