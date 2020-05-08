package com.viettel.backend.service.common;

import reactor.bus.Event;

public class WebsocketSendEvent extends Event<String> {

    public static final String EVENT_NAME = "WebsocketSendEvent";
    
    private static final long serialVersionUID = -3610917001963504563L;

    private String recipientId;
    private String destination;
    private String type;
    private Object content;

    public WebsocketSendEvent(String senderId, String recipientId, String destination, String type, Object content) {
        super(senderId);
        this.recipientId = recipientId;
        this.destination = destination;
        this.type = type;
        this.content = content;
    }
    
    public String getSenderId() {
        return getData();
    }

    public String getRecipientId() {
        return recipientId;
    }

    public String getDestination() {
        return destination;
    }

    public String getType() {
        return type;
    }

    public Object getContent() {
        return content;
    }

}
