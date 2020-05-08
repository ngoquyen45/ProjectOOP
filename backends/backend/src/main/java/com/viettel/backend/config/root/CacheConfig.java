package com.viettel.backend.config.root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import com.viettel.backend.engine.cache.CacheManager;
import com.viettel.backend.repository.common.CacheRepository;

@Configuration
public class CacheConfig {

    @Autowired
	private RedisConnectionFactory redisConnectionFactory;

    @Bean
    public CacheManager cacheManager() {
        return new CacheManager(redisConnectionFactory);
    }
    
    @Bean
    public CacheRepository cacheRepository() {
        return new CacheRepository(cacheManager());
    }
    
}
