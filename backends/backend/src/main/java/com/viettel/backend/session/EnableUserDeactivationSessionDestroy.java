package com.viettel.backend.session;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author thanh
 */
@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value = { java.lang.annotation.ElementType.TYPE })
@Documented
@Configuration
@Import(UserDeactivationSessionDestroyConfiguration.class)
public @interface EnableUserDeactivationSessionDestroy {

}
