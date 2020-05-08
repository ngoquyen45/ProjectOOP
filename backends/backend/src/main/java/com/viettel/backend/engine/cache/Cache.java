package com.viettel.backend.engine.cache;

import static org.springframework.util.Assert.hasText;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.CollectionUtils;

public abstract class Cache<K> {
    
    @SuppressWarnings("rawtypes")
    protected final RedisTemplate template;
    protected final String cacheName;
    protected final byte[] keyPrefix;
    
    public Cache(String name, byte[] prefix, RedisTemplate<? extends Object, ? extends Object> template) {
        hasText(name, "non-empty cache name is required");
        this.cacheName = name;
        this.keyPrefix = prefix;
        this.template = template;
    }

    public String getName() {
        return cacheName;
    }
    
    @SuppressWarnings("unchecked")
    public void clear() {
        template.execute(new RedisCacheCleanByPrefixCallback(keyPrefix), true);
    }
    
    @SuppressWarnings("unchecked")
    public void delete(K key) {
        this.template.delete(key);
    }
    
    @SuppressWarnings("unchecked")
    public void delete(Collection<? extends K> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }
        Set<K> byteKeys = new HashSet<K>(keys.size());
        for (K key : keys) {
            byteKeys.add(key);
        }
        this.template.delete(byteKeys);
    }
    
    @SuppressWarnings("unchecked")
    public boolean hasKey(K key) {
        return this.template.hasKey(key);
    }
    
    /**
     * @author Christoph Strobl
     * @since 1.5
     */
    static class RedisCacheCleanByPrefixCallback implements RedisCallback<Void> {

        private static final byte[] REMOVE_KEYS_BY_PATTERN_LUA = new StringRedisSerializer()
                .serialize("local keys = redis.call('KEYS', ARGV[1]); local keysCount = table.getn(keys); if(keysCount > 0) then for _, key in ipairs(keys) do redis.call('del', key); end; end; return keysCount;");
        private static final byte[] WILD_CARD = new StringRedisSerializer().serialize("*");
        
        private byte[] keyPrefix;

        public RedisCacheCleanByPrefixCallback(byte[] keyPrefix) {
            this.keyPrefix = keyPrefix;
        }

        @Override
        public Void doInRedis(RedisConnection connection) throws DataAccessException {
            byte[] prefixToUse = Arrays.copyOf(keyPrefix, keyPrefix.length + WILD_CARD.length);
            System.arraycopy(WILD_CARD, 0, prefixToUse, keyPrefix.length, WILD_CARD.length);

            connection.eval(REMOVE_KEYS_BY_PATTERN_LUA, ReturnType.INTEGER, 0, prefixToUse);

            return null;
        }
        
    }
    
}
