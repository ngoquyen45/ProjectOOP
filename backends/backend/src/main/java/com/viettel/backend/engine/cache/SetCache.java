package com.viettel.backend.engine.cache;

import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

public class SetCache<K, V> extends Cache<K> {
    
    public SetCache(String name, byte[] prefix, RedisTemplate<? extends Object, ? extends Object> template) {
        super(name, prefix, template);
    }

    @SuppressWarnings("unchecked")
    public BoundSetOperations<K, V> get(K key) {
        return template.boundSetOps(key);
    }
    
}
