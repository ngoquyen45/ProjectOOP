package com.viettel.backend.config.security.captcha;

public interface LoginAttemptRepository {

    int countLoginAttempts(String username);
    
    void increaseLoginAttempt(String username);
    
    void clearLoginAttempts(String username);
    
}
