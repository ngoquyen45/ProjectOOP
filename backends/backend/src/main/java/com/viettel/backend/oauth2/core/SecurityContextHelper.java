package com.viettel.backend.oauth2.core;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

/**
 * @author thanh
 */
public class SecurityContextHelper {

    public static UserLogin getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2Authentication) {
            return extractUserLogin((OAuth2Authentication) authentication);
        }
        return null;
    }

    public static String getCurrentUserId() {
        return SecurityContextHelper.getCurrentUser().getUserId().toString();
    }

    public static boolean isUserInRole(String role) {
        UserLogin userLogin = getCurrentUser();
        if (userLogin == null) {
            return false;
        }
        return userLogin.isRole(role);
    }
    
    public static UserLogin extractUserLogin(OAuth2Authentication oauth) {
        if (oauth == null) {
            return null;
        }
        UserAuthenticationToken auth = (UserAuthenticationToken) oauth.getUserAuthentication();
        return auth.getUserLogin();
    }
    
    /**
     * Check if user already logged via login page
     */
    public static boolean isLoggedInAuthorizationServer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof UserAuthenticationToken) {
            return true;
        }
        return false;
    }
    
}
