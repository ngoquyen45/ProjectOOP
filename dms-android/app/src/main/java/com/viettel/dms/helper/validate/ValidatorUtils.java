package com.viettel.dms.helper.validate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatorUtils {
    
    private static final String STR_EMAIL_PATTERN = 
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    
    private static final String STR_PHONE_PATTERN = 
            "^[\\+]?[0-9]+[0-9\\-]*[0-9]+$";
    
    private static final Pattern emailPattern = Pattern.compile(STR_EMAIL_PATTERN);
    
    private static final Pattern phonePattern = Pattern.compile(STR_PHONE_PATTERN);
    
    public static boolean isValidEmail(String email) {
        Matcher matcher = emailPattern.matcher(email);
        return matcher.matches();
    }
    
    public static boolean isValidPhone(String phone) {
        Matcher matcher = phonePattern.matcher(phone);
        return matcher.matches();
    }
}
