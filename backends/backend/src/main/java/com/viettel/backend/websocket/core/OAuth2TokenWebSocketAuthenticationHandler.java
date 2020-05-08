package com.viettel.backend.websocket.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cliffc.high_scale_lib.NonBlockingHashMap;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.util.Assert;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import com.google.common.util.concurrent.Striped;
import com.viettel.backend.oauth2.core.SecurityContextHelper;
import com.viettel.backend.oauth2.core.TokenDestroyedEvent;
import com.viettel.backend.oauth2.core.UserLogin;

/**
 * @author thanh
 */
public class OAuth2TokenWebSocketAuthenticationHandler implements WebSocketAuthenticationHandler, ApplicationListener<TokenDestroyedEvent> {
    
    private static final Log logger = LogFactory.getLog(OAuth2TokenWebSocketAuthenticationHandler.class);

    private TokenStore tokenStore;
    
    private final BlockingQueue<SessionExpiry> delayed = new DelayQueue<SessionExpiry>();
    
    private int numberStripes = 100;
    private Striped<Lock> lazyWeakStripedLockProvider = Striped.lazyWeakLock(numberStripes);
    
    private final ConcurrentMap<String, List<WebSocketSession>> accessTokenToWebSocketSessionStore = new NonBlockingHashMap<String, List<WebSocketSession>>();
    
    private final ConcurrentMap<WebSocketSession, String> webSocketSessionToAccessTokenStore = new ConcurrentHashMap<WebSocketSession, String>();

    public OAuth2TokenWebSocketAuthenticationHandler(TokenStore tokenStore) {
        Assert.notNull(tokenStore, "TokenStore cannot be null");
        this.setTokenStore(tokenStore);
    }

    @Override
    public void handleWebSocketSessionAuthentication(WebSocketSession session, String login, String passcode) {

        // Passcode was an valid OAuth2 access token
        OAuth2AccessToken token = tokenStore.readAccessToken(passcode);
        if (token == null || token.isExpired()) {
            throw new BadCredentialsException("Invalid token");
        }

        OAuth2Authentication auth = tokenStore.readAuthentication(passcode);
        UserLogin userLogin = SecurityContextHelper.extractUserLogin(auth);
        
        // Should not happend
        if (userLogin == null) {
            logger.error("Cannot extract UserLogin from OAuth2Authentication");
            throw new BadCredentialsException("Unknow error");
        }
        
        // Using user's id for websockets
        session.getAttributes().put(StompLoginSubProtocolWebSocketHandler.SESSION_KEY_USERNAME, userLogin.getUserId().toString());
        session.getAttributes().put(StompLoginSubProtocolWebSocketHandler.SESSION_KEY_VERYFIED, true);
        
        // If token have expiration, schedule to remove this session
        if (token.getExpiration() != null) {
        	SessionExpiry sessionExpiry = new SessionExpiry(session, token.getExpiration());
        	delayed.offer(sessionExpiry);
        }

        webSocketSessionToAccessTokenStore.put(session, token.getValue());
        put(token.getValue(), session);
    }

    @Override
    public void handleWebSocketSessionClosed(WebSocketSession session) {
        String token = webSocketSessionToAccessTokenStore.remove(session);
        remove(token, session);
    }
    
    private void put(String token, WebSocketSession session) {
        if (token == null) {
            return;
        }
        List<WebSocketSession> list = new LinkedList<WebSocketSession>();
        list.add(session);
        List<WebSocketSession> oldList = accessTokenToWebSocketSessionStore.putIfAbsent(token, list);
        if (oldList != null) {
            Lock lock = lazyWeakStripedLockProvider.get(token);
            try {
                lock.lock();
                list = accessTokenToWebSocketSessionStore.get(token);
                if (list == null || list.isEmpty()) {
                    list = new LinkedList<WebSocketSession>();
                } else {
                    list = new LinkedList<WebSocketSession>(list);
                }
                list.add(session);
                accessTokenToWebSocketSessionStore.put(token, list);
            } finally {
                lock.unlock();
            }
        }
    }
    
    private void remove(String token, WebSocketSession session) {
        if (token == null) {
            return;
        }
        Lock lock = lazyWeakStripedLockProvider.get(token);
        try {
            lock.lock();
            if (accessTokenToWebSocketSessionStore.containsKey(token)) {
                List<WebSocketSession> sessions = accessTokenToWebSocketSessionStore.get(token);
                if (sessions != null && session != null && sessions.contains(session)) {
                    sessions.remove(session);
                    if (sessions.isEmpty()) {
                        accessTokenToWebSocketSessionStore.remove(token);
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }
    
    @Scheduled(fixedRate = 10000)
    public void removeExpiredSession() {
        final Collection<SessionExpiry> expired = new ArrayList<SessionExpiry>();
        delayed.drainTo(expired);
        boolean debugEnable = logger.isDebugEnabled();
        for (SessionExpiry expiry: expired) {
            try {
                expiry.getSession().close(CloseStatus.SESSION_NOT_RELIABLE);
            } catch (IOException e) {
                if (debugEnable) {
                    logger.debug("Cannot close websocket session " + expiry.getSession().getId(), e);
                }
            }
        }
    }

    @Override
    public void onApplicationEvent(TokenDestroyedEvent event) {
        String token = event.getToken();
        List<WebSocketSession> sessions = accessTokenToWebSocketSessionStore.remove(token);
        if (sessions != null && !sessions.isEmpty()) {
            int size = sessions.size();
            for (int i = 0; i < size; i++) {
                WebSocketSession session = sessions.get(i);
                try {
                    session.close(CloseStatus.SESSION_NOT_RELIABLE);
                } catch (IOException e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Cannot close websocket session " + session.getId(), e);
                    }
                }
            }
        }
    }

    public TokenStore getTokenStore() {
        return tokenStore;
    }

    public void setTokenStore(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    private static class SessionExpiry implements Delayed {

        private final long expiry;

        private final WebSocketSession session;

        public SessionExpiry(WebSocketSession session, Date date) {
            this.session = session;
            this.expiry = date.getTime();
        }

        public int compareTo(Delayed other) {
            if (this == other) {
                return 0;
            }
            long diff = getDelay(TimeUnit.MILLISECONDS) - other.getDelay(TimeUnit.MILLISECONDS);
            return (diff == 0 ? 0 : ((diff < 0) ? -1 : 1));
        }

        public long getDelay(TimeUnit unit) {
            return expiry - System.currentTimeMillis();
        }

        public WebSocketSession getSession() {
            return session;
        }
    }
}
