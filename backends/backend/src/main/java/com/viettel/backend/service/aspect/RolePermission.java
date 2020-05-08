package com.viettel.backend.service.aspect;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RolePermission {
    
    String[] value() default {};

}
