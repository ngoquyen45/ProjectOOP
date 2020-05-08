package com.viettel.backend.domain.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface UseCode {
    
    boolean generate() default false;

}
