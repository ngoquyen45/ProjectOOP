package com.viettel.backend.config.security;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.AllowFromStrategy;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter.XFrameOptionsMode;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import com.viettel.backend.config.root.FrontendProperties;
import com.viettel.backend.config.security.captcha.AttemptCountAuthenticationSuccessHandler;
import com.viettel.backend.config.security.captcha.RecaptchaAuthenticationFilter;
import com.viettel.backend.oauth2.core.SignedRequestMatcher;
import com.viettel.backend.oauth2.core.UserAuthenticationProvider;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(TotpProperties.class)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    
    @Autowired
    private FrontendProperties frontendProperties;
    
    @Autowired(required = false)
    private RecaptchaAuthenticationFilter recaptchaAuthenticationFilter;
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(userAuthenticationProvider());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                "/401",
                "/404",
                "/500",
        		"/images/**", 
        		"/assets/**",
        		"/api/image/**",
        		"/api/notoken/**",
        		"/websocket/**");
        // Ignore check access token with any request start with /api and have "ticket" parameter
        web.ignoring().requestMatchers(new SignedRequestMatcher("/api/**"));
    }
    
    @Bean
    public AuthenticationProvider userAuthenticationProvider() {
    	UserAuthenticationProvider authenticationProvider = new UserAuthenticationProvider();
    	return authenticationProvider;
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	// Note (ThanhNV60): Role should not start with ROLE_ because it
    	// will be inserted automatically
    	
        // @formatter:off
        http
            .authorizeRequests().antMatchers("/account/login", "/account/ping").permitAll().and()
            .authorizeRequests()
                .anyRequest().hasRole("USER")
                .and()
            .exceptionHandling()
                .accessDeniedPage("/account/login?authorization_error=true")
                .and()
            // TODO (Thanh): put CSRF protection back into this endpoint
            .csrf()
                .requireCsrfProtectionMatcher(new AntPathRequestMatcher("/oauth/authorize")).disable()
            .logout()
                .logoutSuccessUrl("/account/login")
                .logoutUrl("/account/logout")
                .deleteCookies("SESSIONID", CookieLocaleResolver.DEFAULT_COOKIE_NAME)
                .permitAll()
                .and()
            .formLogin()
                    .usernameParameter("j_username")
                    .passwordParameter("j_password")
                    .failureUrl("/account/login?authentication_error=true")
                    .loginPage("/account/login")
                    .loginProcessingUrl("/account/login")
                    .defaultSuccessUrl(frontendProperties.getWebUrl())
                 ;
        // If no allowed origin, mean only allow from same domain
        if (!CollectionUtils.isEmpty(frontendProperties.getAllowedOrigins())) {
        	http
        		.headers()
        			.addHeaderWriter(new XFrameOptionsHeaderWriter(allowFromStrategy()));
        } else {
        	http
    			.headers()
    				.addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsMode.SAMEORIGIN));
        }
        
        // If CAPTCHA enabled, add filter
        if (recaptchaAuthenticationFilter != null) {
            AttemptCountAuthenticationSuccessHandler authenticationSuccessHandler = 
                    new AttemptCountAuthenticationSuccessHandler(recaptchaAuthenticationFilter.getLoginAttemptRepository());
            authenticationSuccessHandler.setDefaultTargetUrl(frontendProperties.getWebUrl());
            http
                .addFilterBefore(recaptchaAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin()
                    .successHandler(authenticationSuccessHandler);
                ;
        }
        // @formatter:on
    }
    
    @Bean
    public AllowFromStrategy allowFromStrategy() {
		return new AllowFromStrategyImpl(frontendProperties.getAllowedOrigins());
	}
    
    public class AllowFromStrategyImpl implements AllowFromStrategy {
    	
    	private final Logger logger = LoggerFactory.getLogger(this.getClass());

		private Collection<String> allowedOrigins = Collections.emptyList();

		public AllowFromStrategyImpl(Collection<String> alloweds) {
			super();
			Assert.notEmpty(alloweds, "Allowed origins cannot be empty.");
			if (!CollectionUtils.isEmpty(allowedOrigins)) {
				this.allowedOrigins = new ArrayList<String>(allowedOrigins.size());
				for (String origin : allowedOrigins) {
					this.allowedOrigins.add(getOrigin(origin));
				}
			}
		}

		protected boolean isAllowed(String origin) {
			if (StringUtils.isEmpty(origin)) {
				return false;
			}
			return allowedOrigins.contains(origin);
		}

		@Override
		public String getAllowFromValue(HttpServletRequest request) {
			String origin = request.getHeader("Origin");
			if (isAllowed(origin)) {
				return origin;
			}
			return null;
		}

		private String getOrigin(String url) {
			if (!url.startsWith("http:") && !url.startsWith("https:")) {
				return url;
			}

			String origin = null;

			try {
				URI uri = new URI(url);
				origin = uri.getScheme() + "://" + uri.getAuthority();
			} catch (URISyntaxException e) {
				logger.error("Cannot parse URI from " + url, e);
			}

			return origin;
		}

	}
    
}
