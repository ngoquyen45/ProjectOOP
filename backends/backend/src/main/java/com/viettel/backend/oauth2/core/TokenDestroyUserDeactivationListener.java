package com.viettel.backend.oauth2.core;

import java.util.Collection;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;

import com.viettel.backend.service.common.UserDeactivationEvent;

import reactor.bus.EventBus;
import reactor.bus.selector.Selectors;
import reactor.fn.Consumer;

/**
 * Listen for {@link UserDeactivationEvent} and destroy all sessions associated with this user
 * 
 * @author thanh
 */
public class TokenDestroyUserDeactivationListener implements InitializingBean, Consumer<UserDeactivationEvent> {
    
    private EventBus eventBus;
    
    // XXX(Thanh): If using more than one clientId, switch to a Collection or event a Provider
    // to allow us to iterate over all possible clientId
    private String clientId;
    private TokenStore tokenStore;
    
    public TokenDestroyUserDeactivationListener(EventBus eventBus, TokenStore tokenStore, String clientId) {
        this.eventBus = eventBus;
        this.tokenStore = tokenStore;
        this.clientId = clientId;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        eventBus.on(Selectors.$(UserDeactivationEvent.EVENT_NAME), this);
    }

    @Override
    public void accept(UserDeactivationEvent event) {
        Collection<OAuth2AccessToken> tokens = tokenStore.findTokensByClientIdAndUserName(clientId, event.getUser().getUsernameFull());
        if (tokens == null || tokens.isEmpty()) {
            return;
        }
        for (OAuth2AccessToken accessToken : tokens) {
            if (accessToken.getRefreshToken() != null) {
                tokenStore.removeRefreshToken(accessToken.getRefreshToken());
            }
            tokenStore.removeAccessToken(accessToken);
        }
    }

}
