package com.viettel.backend.oauth2.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.util.Assert;

/**
 * @author thanh
 */
public class TokenMessageListener implements MessageListener {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private final ApplicationEventPublisher eventPublisher;
    
    /**
     * Creates a new instance
     *
     * @param eventPublisher the {@link ApplicationEventPublisher} to use. Cannot be null.
     */
    public TokenMessageListener(ApplicationEventPublisher eventPublisher) {
        Assert.notNull(eventPublisher, "eventPublisher cannot be null");
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        byte[] messageChannel = message.getChannel();
        byte[] messageBody = message.getBody();
        if(messageChannel == null || messageBody == null) {
            return;
        }
        String channel = new String(messageChannel);
        if(!(channel.endsWith(":del") || channel.endsWith(":expired"))) {
            return;
        }
        String body = new String(messageBody);
        if(!body.startsWith(RedisTokenStore.ACCESS)) {
            return;
        }

        int beginIndex = body.lastIndexOf(":") + 1;
        int endIndex = body.length();
        String token = body.substring(beginIndex, endIndex);

        if(logger.isDebugEnabled()) {
            logger.debug("Publishing TokenDestroyedEvent for token " + token);
        }

        publishEvent(new TokenDestroyedEvent(this, token));
    }

    private void publishEvent(ApplicationEvent event) {
        try {
            this.eventPublisher.publishEvent(event);
        }
        catch (Throwable ex) {
            logger.error("Error publishing " + event + ".", ex);
        }
    }

}
