package com.viettel.backend.config.root;

import java.io.File;
import java.lang.reflect.Field;
import java.util.concurrent.ExecutorService;

import org.apache.commons.lang.SystemUtils;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import com.viettel.backend.config.root.RedisEmbeddedProperties.Embedded;

import redis.embedded.RedisServer;
import redis.embedded.RedisServerBuilder;

@Configuration
@EnableConfigurationProperties
@ConditionalOnProperty(name = "spring.redis.embedded.enable", matchIfMissing = false)
public class RedisConfig implements DisposableBean {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private RedisServer redisServer;
    
    @Bean(name = "org.springframework.autoconfigure.redis.RedisProperties")
    public RedisEmbeddedProperties redisProperties() {
        return new RedisEmbeddedProperties();
    }
    
    @Bean
    public RedisServer embeddedRedisServer() throws Exception {
        createEmbeddedRedis();
        return redisServer;
    }
    
    @Override
    public void destroy() {
        if (redisServer != null) {
            logger.info("Destroying embedded Redis server");
            
            redisServer.stop();

            // TODO (ThanhNV60): Remove this by updating version of Embedded Redis (not release yet, current ver 0.6)
            // Workaround to prevent thread leak (that cause JVM cannot be destroyed)
            try {
                logger.info("Shutting down executor in RedisServer");
                Field leakedExecutorField = ReflectionUtils.findField(redisServer.getClass(), "executor");
                if (leakedExecutorField != null) {
                    leakedExecutorField.setAccessible(true);
                    ExecutorService leakedExecutorService = (ExecutorService) leakedExecutorField.get(redisServer);
                    if (leakedExecutorService != null && !leakedExecutorService.isShutdown()) {
                        leakedExecutorService.shutdown();
                        logger.info("RedisServer shutdown completed");
                    }
                }
            } catch (IllegalArgumentException e) {
                logger.error("Cannot stop ExecutorService of RedisServer. This cause JVM cannot be destroyed");
            } catch (IllegalAccessException e) {
                logger.error("Cannot stop ExecutorService of RedisServer. This cause JVM cannot be destroyed");
            }
        }
    }

    private void createEmbeddedRedis() throws Exception {
        
        Embedded embeddedProperties = redisProperties().getEmbedded();
        
        if (!embeddedProperties.isEnable()) {
            throw new InvalidOperationException("Cannot create embedded Redis server when 'redis.embedded.enable' set to false");
        }
        
        if (redisServer != null) {
            return;
        }

        RedisServerBuilder builder = RedisServer.builder();
        builder.port(redisProperties().getPort());
        
        if (StringUtils.isEmpty(embeddedProperties.getConfigFile())) {
            builder.setting("daemonize no");
            builder.setting("tcp-backlog 511");
            builder.setting("timeout 0");
            builder.setting("tcp-keepalive 60");
            builder.setting("loglevel notice");
            builder.setting("save 900 1");
            builder.setting("save 300 10");
            builder.setting("save 60 10000");
            builder.setting("stop-writes-on-bgsave-error yes");
            builder.setting("rdbcompression yes");
            builder.setting("rdbchecksum yes");
            builder.setting("dbfilename dump.rdb");

            builder.setting("logfile \"\"");
            
            if (!StringUtils.isEmpty(embeddedProperties.getWorkingDir())) {
                String embeddedWorkingDir = embeddedProperties.getWorkingDir();
                if (embeddedWorkingDir.endsWith(File.separator)) {
                    embeddedWorkingDir = embeddedWorkingDir.substring(0, embeddedWorkingDir.length() - 1);
                }
                builder.setting("dir '" + embeddedWorkingDir + "'");
            }
            
            // Windows require maxmemory config
            if (SystemUtils.IS_OS_WINDOWS && StringUtils.isEmpty(embeddedProperties.getMaxMemory())) {
            	builder.setting("maxmemory 1gb");
            }
            
            if (!StringUtils.isEmpty(embeddedProperties.getMaxMemory())) {
            	builder.setting("maxmemory " + embeddedProperties.getMaxMemory());
            }
            builder.setting("appendonly yes");
            builder.setting("appendfilename \"appendonly.aof\"");
            builder.setting("appendfsync everysec");
            builder.setting("no-appendfsync-on-rewrite no");
            builder.setting("auto-aof-rewrite-percentage 100");
            builder.setting("auto-aof-rewrite-min-size 64mb");
            builder.setting("aof-load-truncated yes");
            builder.setting("lua-time-limit 5000");
            builder.setting("hz 10");
            builder.setting("aof-rewrite-incremental-fsync yes");
        } else {
            builder.configFile(embeddedProperties.getConfigFile());
        }
        
        redisServer = builder.build();
        redisServer.start();
        
    }
    
}
