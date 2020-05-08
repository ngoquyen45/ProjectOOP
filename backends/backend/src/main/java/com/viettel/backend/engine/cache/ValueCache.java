package com.viettel.backend.engine.cache;

import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;

public class ValueCache<K, V> extends Cache<K> {
    
    public ValueCache(String name, byte[] prefix, RedisTemplate<? extends Object, ? extends Object> template) {
        super(name, prefix, template);
    }

    @SuppressWarnings("unchecked")
    public BoundValueOperations<K, V> get(K key) {
        return template.boundValueOps(key);
    }

}