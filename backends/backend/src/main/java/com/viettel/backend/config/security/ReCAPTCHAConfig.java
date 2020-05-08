package com.viettel.backend.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.web.client.RestTemplate;

import com.github.mkopylec.recaptcha.testing.TestRecaptchaValidator;
import com.github.mkopylec.recaptcha.validation.RecaptchaValidator;
import com.viettel.backend.config.security.captcha.LoginAttemptRepository;
import com.viettel.backend.config.security.captcha.RecaptchaAuthenticationFilter;
import com.viettel.backend.config.security.captcha.RecaptchaProperties;
import com.viettel.backend.config.security.captcha.RedisLoginAttemptRepository;

@Configuration
@EnableConfigurationProperties(RecaptchaProperties.class)
@ConditionalOnProperty(name = "recaptcha.security.enable", matchIfMissing = true)
public class ReCAPTCHAConfig {
    
    @Autowired
    private RecaptchaProperties recaptchaProperties;
    
    @Autowired(required = false)
    private RecaptchaValidator recaptchaValidator;
    
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Bean
    public RecaptchaAuthenticationFilter recaptchaAuthenticationFilter() {
        return new RecaptchaAuthenticationFilter(recaptchaValidator, recaptchaProperties, loginAttemptRepository());
    }
    
    @Bean
    public LoginAttemptRepository loginAttemptRepository() {
        return new RedisLoginAttemptRepository(redisConnectionFactory);
    }

    @Configuration("recaptchaValidationConfiguration")
    @EnableConfigurationProperties(RecaptchaProperties.class)
    @ConditionalOnProperty(name = "recaptcha.testing.enabled", havingValue = "false", matchIfMissing = true)
    public static class ValidationConfiguration {

        @Autowired
        private RecaptchaProperties recaptchaProperties;

        @Bean
        @ConditionalOnMissingBean
        public RecaptchaValidator userResponseValidator(RestTemplate restTemplate) {
            return new RecaptchaValidator(restTemplate, recaptchaProperties.getValidation());
        }

        @Bean
        @ConditionalOnMissingBean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }
    }
    
    @Configuration("recaptchaTestingConfiguration")
    @EnableConfigurationProperties(RecaptchaProperties.class)
    @ConditionalOnProperty(name = "recaptcha.testing.enabled")
    public static class TestingConfiguration {

        @Autowired
        private RecaptchaProperties recaptchaProperties;

        @Bean
        @ConditionalOnMissingBean
        public RecaptchaValidator userResponseValidator() {
            return new TestRecaptchaValidator(recaptchaProperties.getTesting());
        }
    }
    
}
