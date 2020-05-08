package com.viettel.backend.engine.cache;

import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

public class ZSetCache<K, V> extends Cache<K> {
    
    public ZSetCache(String name, byte[] prefix, RedisTemplate<? extends Object, ? extends Object> template) {
        super(name, prefix, template);
    }

    @SuppressWarnings("unchecked")
    public BoundZSetOperations<K, V> get(K key) {
        return template.boundZSetOps(key);
    }
    
}
