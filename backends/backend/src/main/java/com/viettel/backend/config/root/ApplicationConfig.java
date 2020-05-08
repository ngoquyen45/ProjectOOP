package com.viettel.backend.config.root;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import com.viettel.backend.domain.Promotion;
import com.viettel.backend.domain.embed.OrderProduct;
import com.viettel.backend.domain.embed.OrderPromotion;
import com.viettel.backend.domain.embed.OrderPromotionDetail;
import com.viettel.backend.domain.embed.OrderPromotionReward;
import com.viettel.backend.domain.embed.PromotionDetail;
import com.viettel.backend.engine.file.FileEngine;
import com.viettel.backend.engine.file.MongodbFileEngine;
import com.viettel.backend.engine.notification.WebNotificationEngine;
import com.viettel.backend.engine.notification.WebNotificationEngineImpl;
import com.viettel.backend.engine.promotion.PromotionEngine;
import com.viettel.backend.oauth2.core.DefaultSignedRequestConverter;
import com.viettel.backend.oauth2.core.SignedRequestConverter;
import com.viettel.backend.repository.FileRepository;

import reactor.Environment;
import reactor.bus.EventBus;
import reactor.spring.context.config.EnableReactor;

@Configuration
@ComponentScan(basePackages = { 
        "com.viettel.backend.config.root", 
        "com.viettel.backend.repository",
        "com.viettel.backend.service"
        })
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableScheduling
@EnableReactor
@EnableConfigurationProperties({ FrontendProperties.class, AppProperties.class })
public class ApplicationConfig {
    
    @Value("${oauth2.signed-request-verifier-key}")
    private String signedRequestVerifierKey;
    
    @Autowired
    @Lazy
    private FileRepository fileRepository;

    @Bean
    public MessageSource messageSource() {
        final ReloadableResourceBundleMessageSource ret = new ReloadableResourceBundleMessageSource();
        ret.setBasename("classpath:language");
        ret.setDefaultEncoding("UTF-8");
        return ret;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }
    
    @Bean
    public SignedRequestConverter signedRequestConverter() {
        DefaultSignedRequestConverter signedRequestConverter = new DefaultSignedRequestConverter();
        if (!StringUtils.isEmpty(signedRequestVerifierKey)) {
            signedRequestConverter.setSigningKey(signedRequestVerifierKey);
        }
        return signedRequestConverter;
    }

    @Bean
    @SuppressWarnings("rawtypes")
    public PromotionEngine promotionEngine() {
        return new PromotionEngine<ObjectId, OrderProduct, Promotion, PromotionDetail, OrderPromotion, 
                OrderPromotionDetail, OrderPromotionReward>();
    }
    
    @Bean
    @Lazy
    public FileEngine fileEngine() {
        return new MongodbFileEngine(fileRepository);
    }
    
    @Bean
    public EventBus createEventBus(Environment env) {
        return EventBus.create(env, Environment.THREAD_POOL);
    }
    
    @Bean
    @Lazy
    public WebNotificationEngine webNotificationEngine() {
        return new WebNotificationEngineImpl();
    }

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };
        tomcat.addAdditionalTomcatConnectors(getHttpConnector());
        return tomcat;
    }

    private Connector getHttpConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setPort(8080);
        connector.setSecure(false);
        connector.setRedirectPort(8443);
        return connector;
    }
}
