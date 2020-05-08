package com.viettel.backend.websocket.core;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.StringUtils;

import com.viettel.backend.service.common.WebsocketSendEvent;

import reactor.bus.EventBus;
import reactor.bus.selector.Selectors;
import reactor.fn.Consumer;

/**
 * Listen for {@link WebsocketSendEvent} and notify user via Websockets
 * 
 * @author thanh
 */
public class WebsocketSendEventListener implements InitializingBean, Consumer<WebsocketSendEvent> {
    
    private SimpMessagingTemplate messagingTemplate;
    
    private EventBus eventBus;
    
    public WebsocketSendEventListener(SimpMessagingTemplate messagingTemplate, EventBus eventBus) {
        this.messagingTemplate = messagingTemplate;
        this.eventBus = eventBus;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        eventBus.on(Selectors.$(WebsocketSendEvent.EVENT_NAME), this);
    }

    @Override
    public void accept(WebsocketSendEvent event) {
        if (StringUtils.isEmpty(event.getRecipientId())) {
            WebsocketUtils.send(messagingTemplate, event.getSenderId(), event.getDestination(), 
                    event.getType(), event.getContent());
        } else {
            WebsocketUtils.sendToUser(messagingTemplate, event.getSenderId(), event.getRecipientId(), 
                    event.getDestination(), event.getType(), event.getContent());
        }
    }

}
