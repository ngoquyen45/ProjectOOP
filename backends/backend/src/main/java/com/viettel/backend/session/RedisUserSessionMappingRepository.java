package com.viettel.backend.session;

import java.util.Collections;
import java.util.Set;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.Assert;

/**
 * An implementation of {@link SessionUserMappingRepository} backed by Redis.
 * @author thanh
 */
public class RedisUserSessionMappingRepository implements UserSessionMappingRepository {
    
    static final String KEY_PREFIX = "spring:session:mapping:";
    static final String USER_TO_SESSION = KEY_PREFIX + "user:";
    static final String SESSION_TO_USER = KEY_PREFIX + "session:";
    
    private final RedisOperations<String, String> sessionRedisOperations;
    private final StringRedisSerializer serializer;
    
    @SuppressWarnings("unchecked")
    public RedisUserSessionMappingRepository(RedisConnectionFactory redisConnectionFactory) {
        this(createDefaultTemplate(redisConnectionFactory));
    }

    public RedisUserSessionMappingRepository(RedisOperations<String, String> sessionRedisOperations) {
        Assert.notNull(sessionRedisOperations, "sessionRedisOperations cannot be null");
        this.sessionRedisOperations = sessionRedisOperations;
        this.serializer = (StringRedisSerializer) this.sessionRedisOperations.getKeySerializer();
    }

    @Override
    public void registerSessionId(final String sessionId, final String userId) {
        final byte[] rawUserToSessionKey = serializer.serialize(USER_TO_SESSION + userId);
        final byte[] rawSessionValue = serializer.serialize(sessionId);
        final byte[] rawSessionToUserKey = serializer.serialize(SESSION_TO_USER + sessionId);
        final byte[] rawUserValue = serializer.serialize(userId);
        
        sessionRedisOperations.executePipelined(new RedisCallback<Void>() {

            @Override
            public Void doInRedis(RedisConnection connection) {
                connection.set(rawSessionToUserKey, rawUserValue);
                connection.sAdd(rawUserToSessionKey, rawSessionValue);
                return null;
            }
        });
    }

    @Override
    public void unregisterSessionId(String sessionId) {
        final byte[] rawSessionToUserKey = serializer.serialize(SESSION_TO_USER + sessionId);
        final byte[] rawSessionValue = serializer.serialize(sessionId);

        final String user = sessionRedisOperations.boundValueOps(SESSION_TO_USER + sessionId).get();
        
        if (user != null) {
            sessionRedisOperations.executePipelined(new RedisCallback<Void>() {

                @Override
                public Void doInRedis(RedisConnection connection) {
                    byte[] rawUserToSessionKey = serializer.serialize(USER_TO_SESSION + user);
                    connection.sRem(rawUserToSessionKey, rawSessionValue);
                    connection.del(rawSessionToUserKey);
                    return null;
                }
            });
        }
    }

    @Override
    public Set<String> getSessionIds(String user) {
        String key = USER_TO_SESSION + user;
        Set<String> entries = this.sessionRedisOperations.boundSetOps(key).members();
        return (entries != null) ? entries : Collections.<String>emptySet();
    }

    @SuppressWarnings("rawtypes")
    private static RedisTemplate createDefaultTemplate(RedisConnectionFactory connectionFactory) {
        Assert.notNull(connectionFactory, "connectionFactory cannot be null");
        StringRedisTemplate template = new StringRedisTemplate(connectionFactory);
        template.afterPropertiesSet();
        return template;
    }

}
