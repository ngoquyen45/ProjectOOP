package com.viettel.backend.config;

import java.util.Arrays;

import javax.servlet.DispatcherType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.MessageSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.OAuth2AutoConfiguration;
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.HttpEncodingAutoConfiguration;
import org.springframework.boot.autoconfigure.web.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.web.MultipartAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.AbstractContextLoaderInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import com.viettel.backend.config.root.ApplicationConfig;

/**
 * Servlet initiator with Spring Http Session enabled Using
 * {@link AbstractContextLoaderInitializer} for default session
 * 
 * @author thanh
 */
@Configuration
@EnableAutoConfiguration(exclude = { 
        MessageSourceAutoConfiguration.class, 
        DispatcherServletAutoConfiguration.class,
        SecurityAutoConfiguration.class,
        OAuth2AutoConfiguration.class,
        WebMvcAutoConfiguration.class,
        MultipartAutoConfiguration.class,
        MongoRepositoriesAutoConfiguration.class,
        SpringDataWebAutoConfiguration.class,
        ActiveMQAutoConfiguration.class,
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HttpEncodingAutoConfiguration.class,
        HttpMessageConvertersAutoConfiguration.class,
        JacksonAutoConfiguration.class,
        JmsAutoConfiguration.class,
        // Exclude reCAPTCHA auto-configuration
        com.github.mkopylec.recaptcha.security.SecurityConfiguration.class,
        com.github.mkopylec.recaptcha.validation.ValidationConfiguration.class,
        com.github.mkopylec.recaptcha.testing.TestingConfiguration.class
})
@Import(ApplicationConfig.class)
@ImportResource("classpath:app-config.xml")
public class ServletInitializer extends SpringBootServletInitializer {
    
    public static void main(String[] args) {
        SpringApplication.run(ServletInitializer.class, args);
    }
    
    @Autowired
    ApplicationContext applicationContext;

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ServletInitializer.class);
    }

    @Bean
    public DispatcherServlet restfulServlet() {
        
        // Create api dispatcher servlet context
        AnnotationConfigWebApplicationContext restfulServletContext = new AnnotationConfigWebApplicationContext();
        restfulServletContext.setParent(applicationContext);
        restfulServletContext.scan("com.viettel.backend.config.restful");

        DispatcherServlet restfulServlet = new DispatcherServlet(restfulServletContext);
        restfulServlet.setThrowExceptionIfNoHandlerFound(true);
        return restfulServlet;
    }

    @Bean
    public ServletRegistrationBean restfulServletRegistration() {
        
        ServletRegistrationBean registration = new ServletRegistrationBean(restfulServlet());
        registration.setName("restfulServlet");
        registration.setLoadOnStartup(1);
        registration.setAsyncSupported(true);
        registration.addUrlMappings("/api/*");

        return registration;
    }

    @Bean(name = DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_BEAN_NAME)
    public DispatcherServlet dispatcherServlet() {
        
        // Create default dispatcher servlet context
        AnnotationConfigWebApplicationContext webServletContext = new AnnotationConfigWebApplicationContext();
        webServletContext.setParent(applicationContext);
        webServletContext.scan("com.viettel.backend.config.web");

        return new DispatcherServlet(webServletContext);
    }

    @Bean(name = DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_REGISTRATION_BEAN_NAME)
    public ServletRegistrationBean dispatcherServletRegistration() {
        
        ServletRegistrationBean registration = new ServletRegistrationBean(dispatcherServlet());
        registration.setLoadOnStartup(1);
        registration.setAsyncSupported(true);
        registration.addUrlMappings("/");

        return registration;
    }

    @Bean
    public FilterRegistrationBean encodingFilterRegistrationBean() {
        
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);
        
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(characterEncodingFilter);
        registrationBean.setUrlPatterns(Arrays.asList("/*"));
        registrationBean.setDispatcherTypes(DispatcherType.REQUEST);
        registrationBean.setOrder(1);
        
        return registrationBean;
    }
    
    @Bean
    public FilterRegistrationBean corsFilterRegistrationBean() {
        
        DelegatingFilterProxy corsFilter = new DelegatingFilterProxy("corsFilter");
        
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(corsFilter);
        registrationBean.setUrlPatterns(Arrays.asList("/*"));
        registrationBean.setDispatcherTypes(DispatcherType.REQUEST);
        registrationBean.setOrder(2);
        
        return registrationBean;
    }
    
    @Bean
    public FilterRegistrationBean securityFilterRegistrationBean() {
        
        DelegatingFilterProxy securityFilter = new DelegatingFilterProxy("springSecurityFilterChain");
        
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(securityFilter);
        registrationBean.setUrlPatterns(Arrays.asList("/*"));
        registrationBean.setName("springSecurityFilterChain");
        registrationBean.addInitParameter("contextAttribute", "org.springframework.web.servlet.FrameworkServlet.CONTEXT.dispatcherServlet");
        registrationBean.setDispatcherTypes(DispatcherType.REQUEST);
        registrationBean.setOrder(3);
        
        return registrationBean;
    }

    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
       return new EmbeddedServletContainerCustomizer() {
            @Override
            public void customize(ConfigurableEmbeddedServletContainer container) {
                ErrorPage error401Page = new ErrorPage(HttpStatus.UNAUTHORIZED, "/401");
                ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/404");
                ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/500");
                
                container.addErrorPages(error401Page, error404Page, error500Page);
            }
       };
    }
}
