package com.viettel.backend.websocket.core;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.BufferingStompDecoder;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompDecoder;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.messaging.StompSubProtocolHandler;
import org.springframework.web.socket.messaging.SubProtocolHandler;
import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler;

/**
 * @author thanh
 */
public class StompLoginSubProtocolWebSocketHandler extends WebSocketHandlerDecorator {

    private WebSocketAuthenticationHandler webSocketAuthenticationProvider;

    private static final Log logger = LogFactory.getLog(StompLoginSubProtocolWebSocketHandler.class);

    public static final String SESSION_KEY_USERNAME = "SESSION_KEY_USERNAME";

    public static final String SESSION_KEY_VERYFIED = "SESSION_KEY_VERYFIED";

    private StompSubProtocolHandler stompSubProtocolHandler;

    private final StompDecoder stompDecoder = new StompDecoder();

    private final Map<String, BufferingStompDecoder> decoders = new ConcurrentHashMap<String, BufferingStompDecoder>();
    
    public StompLoginSubProtocolWebSocketHandler(WebSocketAuthenticationHandler webSocketAuthenticationProvider,
            WebSocketHandler delegate) {
        super(delegate);
        this.webSocketAuthenticationProvider = webSocketAuthenticationProvider;
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> webSocketMessage) throws Exception {
        if (!isSessionVerified(session)) {
            verifySession(session, webSocketMessage);
        }
        super.handleMessage(session, webSocketMessage);
    }

    private void verifySession(WebSocketSession session, WebSocketMessage<?> webSocketMessage) {
        List<Message<byte[]>> messages = null;
        try {
            ByteBuffer byteBuffer = null;
            if (webSocketMessage instanceof TextMessage) {
                byteBuffer = ByteBuffer.wrap(((TextMessage) webSocketMessage).asBytes());
            } else if (webSocketMessage instanceof BinaryMessage) {
                byteBuffer = ((BinaryMessage) webSocketMessage).getPayload();
            } else {
                ;
            }

            BufferingStompDecoder decoder = this.decoders.get(session.getId());

            if (byteBuffer != null && decoder != null) {
                messages = decoder.decode(byteBuffer);
            }

            if (messages != null && !messages.isEmpty()) {
                for (Message<byte[]> message : messages) {
                    StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message,
                            StompHeaderAccessor.class);

                    if (StompCommand.CONNECT.equals(headerAccessor.getCommand())) {
                        try {
                            webSocketAuthenticationProvider.handleWebSocketSessionAuthentication(
                                    session, headerAccessor.getLogin(), headerAccessor.getPasscode());

                            if (logger.isDebugEnabled()) {
                                logger.debug("Successful authenticate for websocket session " + session.getId());
                            }
                        } catch (Exception ex) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Cannot authenticate websockets connection", ex);
                            }
                            session.close(CloseStatus.POLICY_VIOLATION);
                            return;
                        } finally {
                            this.decoders.remove(session.getId());
                        }
                    }
                }
            }
        } catch (Throwable ex) {
            ;
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        super.afterConnectionClosed(session, closeStatus);
        webSocketAuthenticationProvider.handleWebSocketSessionClosed(session);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        WebSocketHandler handler = getLastHandler();
        if (stompSubProtocolHandler == null) {
            if (handler instanceof SubProtocolWebSocketHandler) {
                SubProtocolWebSocketHandler subProtocolWebSocketHandler = (SubProtocolWebSocketHandler) handler;
                for (SubProtocolHandler subProtocolHandler : subProtocolWebSocketHandler.getProtocolHandlers()) {
                    if (subProtocolHandler instanceof StompSubProtocolHandler) {
                        stompSubProtocolHandler = (StompSubProtocolHandler) subProtocolHandler;
                    }
                    break;
                }
            }
        }
        if (isProtocolSupported(session)) {
            this.decoders.put(session.getId(),
                    new BufferingStompDecoder(this.stompDecoder, stompSubProtocolHandler.getMessageSizeLimit()));
        }

        super.afterConnectionEstablished(session);
    }

    public boolean isSessionVerified(WebSocketSession session) {
        return session.getAttributes().get(SESSION_KEY_VERYFIED) != null;
    }

    protected boolean isProtocolSupported(WebSocketSession session) {
        if (stompSubProtocolHandler == null) {
            return false;
        }

        return StringUtils.isEmpty(session.getAcceptedProtocol())
                || stompSubProtocolHandler.getSupportedProtocols().contains(session.getAcceptedProtocol());
    }

    public WebSocketAuthenticationHandler getWebSocketAuthenticationProvider() {
        return webSocketAuthenticationProvider;
    }

    public void setWebSocketAuthenticationProvider(WebSocketAuthenticationHandler webSocketAuthenticationProvider) {
        this.webSocketAuthenticationProvider = webSocketAuthenticationProvider;
    }
}
