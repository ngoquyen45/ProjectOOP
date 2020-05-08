package com.viettel.backend.config.security.captcha;

import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import com.github.mkopylec.recaptcha.security.RecaptchaAuthenticationException;
import com.github.mkopylec.recaptcha.validation.ErrorCode;
import com.github.mkopylec.recaptcha.validation.RecaptchaValidator;

/**
 * Extended version of {@link com.github.mkopylec.recaptcha.security.RecaptchaAuthenticationFilter} that allow to
 * configure some options (eg: enable or not, strict-mode, max-fail-count, etc.)
 * 
 * @author thanh
 */
public class RecaptchaAuthenticationFilter extends com.github.mkopylec.recaptcha.security.RecaptchaAuthenticationFilter {

    public static final String LOGIN_FAIL_COUNT = "recaptcha.LOGIN_FAIL_COUNT";
    public static final String LOGIN_REQUIRE_CAPTCHA = "recaptcha.LOGIN_REQUIRE_CAPTCHA";
    
    private RecaptchaProperties recaptchaProperties;
    private LoginAttemptRepository loginAttemptRepository;
    
    public RecaptchaAuthenticationFilter(RecaptchaValidator recaptchaValidator, RecaptchaProperties recaptchaProperties,
            LoginAttemptRepository loginAttemptRepository) {
        super(recaptchaValidator, recaptchaProperties);
        
        this.setLoginAttemptRepository(loginAttemptRepository);
        this.recaptchaProperties = recaptchaProperties;
    }
    
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        
        String username = request.getParameter(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY);
        
        if (recaptchaProperties.getSecurity().isEnable()) {
            
            int userLoginAttempts = getLoginAttemptRepository().countLoginAttempts(username);
            int sessionLoginAttempts = getSessionAttemptCount(request);

            checkAttempt(request, userLoginAttempts, sessionLoginAttempts);
            
            increateLoginAttempt(request, username);
            
            // If strict-mode or login fail count large than specified value
            if (recaptchaProperties.getSecurity().isStrictMode()
                    || userLoginAttempts > recaptchaProperties.getSecurity().getLoginFailCount()
                    || sessionLoginAttempts > recaptchaProperties.getSecurity().getLoginFailCount()) {
                
                if (noRecaptchaResponse(request)) {
                    throw new RecaptchaAuthenticationException(Collections.singletonList(ErrorCode.MISSING_USER_CAPTCHA_RESPONSE));
                }
                
                return super.attemptAuthentication(request, response);
            }
            
        }
        
        return new PreAuthenticatedAuthenticationToken(RECAPTCHA_AUTHENTICATION_PRINCIPAL, null);
    }

    private boolean noRecaptchaResponse(HttpServletRequest request) {
        return !request.getParameterMap().containsKey(recaptcha.getValidation().getResponseParameter());
    }
    
    private int getSessionAttemptCount(HttpServletRequest request) {
        Object count = request.getSession().getAttribute(LOGIN_FAIL_COUNT);
        return count != null ? (int)count : 0;
    }
    
    private void checkAttempt(HttpServletRequest request, int userLoginAttempts, int sessionLoginAttempts) {
        if (recaptchaProperties.getSecurity().isStrictMode()
                || userLoginAttempts + 1 > recaptchaProperties.getSecurity().getLoginFailCount()
                || sessionLoginAttempts + 1 > recaptchaProperties.getSecurity().getLoginFailCount()) {
            request.getSession().setAttribute(LOGIN_REQUIRE_CAPTCHA, true);
        }
    }
    
    private void increateLoginAttempt(HttpServletRequest request, String username) {
        getLoginAttemptRepository().increaseLoginAttempt(username);
        request.getSession().setAttribute(LOGIN_FAIL_COUNT, getSessionAttemptCount(request) + 1);
    }

    public LoginAttemptRepository getLoginAttemptRepository() {
        return loginAttemptRepository;
    }

    public void setLoginAttemptRepository(LoginAttemptRepository loginAttemptRepository) {
        this.loginAttemptRepository = loginAttemptRepository;
    }

}
