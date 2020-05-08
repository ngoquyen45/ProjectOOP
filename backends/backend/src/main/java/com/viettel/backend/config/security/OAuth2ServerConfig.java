package com.viettel.backend.config.security;

import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import com.viettel.backend.oauth2.core.AccessTokenEnhancer;
import com.viettel.backend.oauth2.core.EnableRedisTokenDestroyBroadcaster;
import com.viettel.backend.oauth2.core.EnableUserDeactivationAccessTokenDestroy;
import com.viettel.backend.oauth2.core.HardCodeClientDetailsService;
import com.viettel.backend.oauth2.core.OAuth2Properties;
import com.viettel.backend.oauth2.core.SimpleUserApprovalHandler;
import com.viettel.backend.oauth2.core.UniqueAuthenticationKeyGenerator;

/**
 * If using Redis as token store, please include <code>@EnableRedisTokenDestroyBroadcaster</code> to listen to
 * <code>TokenDestroyedEvent</code>.
 * If using InMemoryTokenStore, remove this annotation and using extended version <code>DestroyAwareInMemoryTokenStore</code>.
 * This event useful in case we want to close Websockets Sessions when a token is destroyed
 * 
 * @author thanh
 */
@Configuration
@EnableRedisTokenDestroyBroadcaster
@EnableUserDeactivationAccessTokenDestroy
@EnableConfigurationProperties(OAuth2Properties.class)
public class OAuth2ServerConfig {
	
	@Configuration
	@EnableResourceServer
	protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
	    
	    @Autowired
        private OAuth2Properties oAuth2Properties;
		
		@Override
		public void configure(ResourceServerSecurityConfigurer resources) {
			resources.resourceId(oAuth2Properties.getRealm());
		}

		@Override
		public void configure(HttpSecurity http) throws Exception {
			// @formatter:off
			http
			.requestMatchers().antMatchers(
					"/oauth/revoke/**", 
					"/oauth/password",
					"/oauth/userinfo", 
					"/api/**"
					)
			.and()
				.authorizeRequests()
					// Revoke token endpoint
					.regexMatchers(HttpMethod.DELETE, "/oauth/revoke/.*")
						.access("#oauth2.isUser() and hasRole('ROLE_USER')")
						
					// Get user's info endpoint
					.regexMatchers(HttpMethod.GET, "/oauth/userinfo")
						.access("#oauth2.isUser() and hasRole('ROLE_USER')")
					
					// Change password endpoint
					.regexMatchers(HttpMethod.PUT, "/oauth/password")
						.access("#oauth2.isUser() and hasRole('ROLE_USER')")
					
					// Endpoints for role admin
					.regexMatchers(HttpMethod.GET, "/api/admin/.*")
						.access("#oauth2.isUser() and hasRole('ROLE_AD')")
						
					// Endpoints for role salesman
					.regexMatchers(HttpMethod.GET, "/api/salesman/.*")
						.access("#oauth2.isUser() and hasRole('ROLE_SM')")
						
					// Endpoints for role admin
					.regexMatchers(HttpMethod.GET, "/api/superadmin/.*")
						.access("#oauth2.isUser() and hasRole('ROLE_SUPER')")
						
					// Endpoints for role supervisor
					.regexMatchers(HttpMethod.GET, "/api/supervisor/.*")
						.access("#oauth2.isUser() and hasRole('ROLE_SUP')")
						
					// All other endpoint
					.regexMatchers("/.*")
						.access("#oauth2.isUser() and hasRole('ROLE_USER')")
						;
			

			// @formatter:on
		}

	}

	@Configuration
	@EnableAuthorizationServer
	protected static class AuthorizationServerConfiguration extends
			AuthorizationServerConfigurerAdapter {

		@Autowired
		@Qualifier("authenticationManagerBean")
		private AuthenticationManager authenticationManager;
		
		@Autowired
		private RedisConnectionFactory redisConnectionFactory;
		
		@Autowired
		private OAuth2Properties oAuth2Properties;
		
		@Override
		public void configure(ClientDetailsServiceConfigurer clients)
				throws Exception {
			clients.withClientDetails(clientDetailsService());
		}

		@Bean
		public TokenStore tokenStore() {
			// DestroyAwareInMemoryTokenStore tokenStore = new DestroyAwareInMemoryTokenStore();
		    RedisTokenStore tokenStore = new RedisTokenStore(redisConnectionFactory);
			tokenStore.setAuthenticationKeyGenerator(new UniqueAuthenticationKeyGenerator());
			
			return tokenStore;
		}

		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints)
				throws Exception {
			endpoints.tokenStore(tokenStore())
					.userApprovalHandler(userApprovalHandler())
					.tokenEnhancer(tokenEnhancer())
					.authorizationCodeServices(authorizationCodeServices())
					.authenticationManager(authenticationManager)
					.tokenServices(tokenServices());
		}

		@Bean
		@Primary
		public AuthorizationServerTokenServices tokenServices() {
            DefaultTokenServices tokenServices = new DefaultTokenServices();
            tokenServices.setTokenStore(tokenStore());
            tokenServices.setSupportRefreshToken(true);
            tokenServices.setClientDetailsService(clientDetailsService());
            tokenServices.setTokenEnhancer(tokenEnhancer());
            tokenServices.setReuseRefreshToken(false);
            return tokenServices;
        }

        @Bean
		public AuthorizationCodeServices authorizationCodeServices() {
			return new InMemoryAuthorizationCodeServices();
		}

		@Override
		public void configure(AuthorizationServerSecurityConfigurer oauthServer)
				throws Exception {
			oauthServer
					.realm(oAuth2Properties.getRealm())
					.allowFormAuthenticationForClients()
			;
		}

		@Bean
		public ClientDetailsService clientDetailsService() {
			BaseClientDetails clientDetails = new BaseClientDetails();
	        clientDetails.setClientId(oAuth2Properties.getClientId());
	        clientDetails.setClientSecret(oAuth2Properties.getClientSecret());
	        clientDetails.setAuthorizedGrantTypes(oAuth2Properties.getGrantType());
	        clientDetails.setScope(oAuth2Properties.getScope());
	        clientDetails.setRefreshTokenValiditySeconds(oAuth2Properties.getRefreshTokenValidity());
	        clientDetails.setAccessTokenValiditySeconds(oAuth2Properties.getAccessTokenValidity());
	        clientDetails.setRegisteredRedirectUri(new HashSet<String>(oAuth2Properties.getRedirectUri()));
	        // clientDetails.setAuthorities(extractAuthorities(app.getAuthorities()));
	        // clientDetails.setAdditionalInformation(extractAdditionalInformation(app.getAdditionalInformation()));
	        
	        clientDetails.setAutoApproveScopes(clientDetails.getScope());
	        
	        return new HardCodeClientDetailsService(clientDetails);
		}
		
		@Bean
		public TokenEnhancer tokenEnhancer() {
		    return new AccessTokenEnhancer();
		}
		
		@Bean
        public ApprovalStore approvalStore() throws Exception {
            TokenApprovalStore store = new TokenApprovalStore();
            store.setTokenStore(tokenStore());
            return store;
        }

        @Bean
        public SimpleUserApprovalHandler userApprovalHandler() throws Exception {
            SimpleUserApprovalHandler handler = new SimpleUserApprovalHandler();
            handler.setApprovalStore(approvalStore());
            handler.setRequestFactory(new DefaultOAuth2RequestFactory(
                    clientDetailsService()));
            handler.setClientDetailsService(clientDetailsService());
            handler.setUseApprovalStore(true);
            return handler;
        }
		
	}
}
