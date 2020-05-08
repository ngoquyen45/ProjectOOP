package com.viettel.backend.util;

import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordUtils {

    private static final String DEFAULT_PASSWORD = "123456";

    public static String encode(PasswordEncoder encoder, String key) {
        String value = encoder.encode(key);
        return value;
    }
    
    public static String getDefaultPassword(PasswordEncoder encoder) {
        return encode(encoder, DEFAULT_PASSWORD);
    }

    public static boolean matches(PasswordEncoder encoder, CharSequence rawPassword, String encodedPassword) {
        boolean isMactch = encoder.matches(rawPassword, encodedPassword);
        return isMactch;
    }

}
