package com.viettel.backend.config.root;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.data.annotation.Persistent;
import org.springframework.data.mapping.model.FieldNamingStrategy;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import com.mongodb.MongoClientOptions;
import com.viettel.backend.domain.PO;

@Configuration
@EnableConfigurationProperties(MongoClientProperties.class)
public class DatabaseConfig {
    
    @Autowired
    private MongoProperties mongoProperties;
    
    @Autowired
    private MongoClientProperties mongoClientProperties;
    
    private String domainPackageName = ClassUtils.getPackageName(PO.class);
    
    @Bean
    public MongoClientOptions mongoClientOptions() {
        MongoClientOptions.Builder builder = MongoClientOptions.builder();
        builder.connectionsPerHost(mongoClientProperties.getConnectionPerHost());
        builder.threadsAllowedToBlockForConnectionMultiplier(mongoClientProperties.getThreadsAllowedToBlockMultiplier());
        return builder.build();
    }
    
    @Bean
    public CustomConversions customConversions() {
        return new CustomConversions(Collections.emptyList());
    }
    
    @Bean
    public MongoMappingContext mongoMappingContext(BeanFactory beanFactory)
            throws ClassNotFoundException {
        MongoMappingContext context = new MongoMappingContext();
        context.setInitialEntitySet(getInitialEntitySet());
        Class<?> strategyClass = this.mongoProperties.getFieldNamingStrategy();
        if (strategyClass != null) {
            context.setFieldNamingStrategy(
                    (FieldNamingStrategy) BeanUtils.instantiate(strategyClass));
        }
        return context;
    }
    
    /**
     * Scans the mapping base package for classes annotated with {@link Document}.
     * 
     * @see #getMappingBasePackage()
     * @return
     * @throws ClassNotFoundException
     */
    protected Set<Class<?>> getInitialEntitySet() throws ClassNotFoundException {

        Set<Class<?>> initialEntitySet = new HashSet<Class<?>>();

        if (StringUtils.hasText(domainPackageName)) {
            ClassPathScanningCandidateComponentProvider componentProvider = new ClassPathScanningCandidateComponentProvider(
                    false);
            componentProvider.addIncludeFilter(new AnnotationTypeFilter(Document.class));
            componentProvider.addIncludeFilter(new AnnotationTypeFilter(Persistent.class));

            for (BeanDefinition candidate : componentProvider.findCandidateComponents(domainPackageName)) {
                initialEntitySet.add(ClassUtils.forName(candidate.getBeanClassName(),
                        AbstractMongoConfiguration.class.getClassLoader()));
            }
        }

        return initialEntitySet;
    }

}
