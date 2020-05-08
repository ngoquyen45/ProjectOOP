package com.viettel.backend.oauth2.core;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

/**
 * @author thanh
 */
public class AccessTokenEnhancer implements TokenEnhancer {
    
    public static final String KEY_USER = "user";
    public static final String KEY_LANGUAGE = "lang";
    public static final String KEY_AUTO_REFRESH = "auto_refresh";
    
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken,
            OAuth2Authentication authentication) {
        DefaultOAuth2AccessToken newToken = new DefaultOAuth2AccessToken(accessToken);
        if (!authentication.isClientOnly()) {
        	Map<String, Object> info = new LinkedHashMap<String, Object>(accessToken.getAdditionalInformation());
            //info.put(KEY_USER, ((UserAuthenticationToken)authentication.getUserAuthentication()).getUser());
        	
        	// If grant_type is implicit, pass current user's language selection
        	if ("implicit".equals(authentication.getOAuth2Request().getGrantType())) {
        	    // If it is a auto renew token request, pass auto_refresh flag so browser app
        	    // can decise the right way to process this token. And no passing the user's language
        	    // because it may overide the user's selection language in brower app
        	    if (authentication.getOAuth2Request().getRequestParameters().containsKey(KEY_AUTO_REFRESH)) {
        	        info.put(KEY_AUTO_REFRESH, authentication.getOAuth2Request().getRequestParameters().get(KEY_AUTO_REFRESH));
        	    } else {
        	        info.put(KEY_LANGUAGE, LocaleContextHolder.getLocaleContext().getLocale());
        	    }
        	}
            newToken.setAdditionalInformation(info);
        }
        return newToken;
    }
}
