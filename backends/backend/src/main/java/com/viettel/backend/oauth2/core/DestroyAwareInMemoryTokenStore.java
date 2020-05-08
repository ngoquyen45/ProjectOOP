package com.viettel.backend.oauth2.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author thanh
 */
@Transactional
public class DestroyAwareInMemoryTokenStore extends InMemoryTokenStore implements ApplicationEventPublisherAware {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private ApplicationEventPublisher eventPublisher;
    
    @Override
    public void removeAccessToken(String tokenValue) {
        super.removeAccessToken(tokenValue);
        
        publishEvent(new TokenDestroyedEvent(this, tokenValue));
    }
    
    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }
    
    private void publishEvent(ApplicationEvent event) {
        if (this.eventPublisher != null) {
            try {
                this.eventPublisher.publishEvent(event);
            }
            catch (Throwable ex) {
                logger.error("Error publishing " + event + ".", ex);
            }
        }
    }

}
