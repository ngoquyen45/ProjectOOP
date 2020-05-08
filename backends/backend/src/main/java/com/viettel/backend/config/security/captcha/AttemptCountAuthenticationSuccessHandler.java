package com.viettel.backend.config.security.captcha;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * An implementation of {@link AuthenticationSuccessHandler} that clear login attempts from {@link javax.servlet.http.HttpSession}
 * 
 * @author thanh
 */
public class AttemptCountAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    
    private LoginAttemptRepository loginAttemptRepository;
    
    public AttemptCountAuthenticationSuccessHandler(LoginAttemptRepository loginAttemptRepository) {
        super();
        
        this.loginAttemptRepository = loginAttemptRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws ServletException, IOException {
        
        String username = request.getParameter(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY);
        
        request.getSession().removeAttribute(RecaptchaAuthenticationFilter.LOGIN_FAIL_COUNT);
        request.getSession().removeAttribute(RecaptchaAuthenticationFilter.LOGIN_REQUIRE_CAPTCHA);
        
        loginAttemptRepository.clearLoginAttempts(username);
        
        super.onAuthenticationSuccess(request, response, authentication);
    }
    
}
