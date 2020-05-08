package com.viettel.backend.websocket.core;

import org.springframework.messaging.simp.SimpMessagingTemplate;

public class WebsocketUtils {
	
    public static void send(SimpMessagingTemplate template, String senderId, String destination, String type, Object data) {
        OutputMessage message = new OutputMessage(senderId, type, data);
        template.convertAndSend(destination, message);
    }
    
    public static void sendToUser(SimpMessagingTemplate template, String senderId, String recipientId, String destination, String type, Object data) {
        OutputMessage message = new OutputMessage(senderId, type, data);
        template.convertAndSendToUser(recipientId, destination, message);
    }
    
}
