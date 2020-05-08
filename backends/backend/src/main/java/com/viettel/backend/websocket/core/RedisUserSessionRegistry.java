package com.viettel.backend.websocket.core;

import java.util.Collections;
import java.util.Set;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.user.UserSessionRegistry;
import org.springframework.util.Assert;

/**
 * An implementation of {@link UserSessionRegistry} backed by Redis.
 * @author thanh
 */
@SuppressWarnings("deprecation")
public class RedisUserSessionRegistry implements UserSessionRegistry {
    
    /**
     * The prefix for each key of the Redis Set representing a user's sessions. The suffix is the unique user id.
     */
    static final String BOUNDED_SET_KEY_PREFIX = "spring:websockets:users:";

    private final RedisOperations<String, String> sessionRedisOperations;

    @SuppressWarnings("unchecked")
    public RedisUserSessionRegistry(RedisConnectionFactory redisConnectionFactory) {
        this(createDefaultTemplate(redisConnectionFactory));
    }

    public RedisUserSessionRegistry(RedisOperations<String, String> sessionRedisOperations) {
        Assert.notNull(sessionRedisOperations, "sessionRedisOperations cannot be null");
        this.sessionRedisOperations = sessionRedisOperations;
    }

    @Override
    public Set<String> getSessionIds(String user) {
        Set<String> entries = getSessionBoundHashOperations(user).members();
        return (entries != null) ? entries : Collections.<String>emptySet();
    }

    @Override
    public void registerSessionId(String user, String sessionId) {
        getSessionBoundHashOperations(user).add(sessionId);
    }

    @Override
    public void unregisterSessionId(String user, String sessionId) {
        getSessionBoundHashOperations(user).remove(sessionId);
    }
    
    /**
     * Gets the {@link BoundHashOperations} to operate on a username
     */
    private BoundSetOperations<String, String> getSessionBoundHashOperations(String username) {
        String key = getKey(username);
        return this.sessionRedisOperations.boundSetOps(key);
    }

    /**
     * Gets the Hash key for this user by prefixing it appropriately.
     */
    static String getKey(String username) {
        return BOUNDED_SET_KEY_PREFIX + username;
    }

    @SuppressWarnings("rawtypes")
    private static RedisTemplate createDefaultTemplate(RedisConnectionFactory connectionFactory) {
        Assert.notNull(connectionFactory, "connectionFactory cannot be null");
        StringRedisTemplate template = new StringRedisTemplate(connectionFactory);
        template.afterPropertiesSet();
        return template;
    }

}
