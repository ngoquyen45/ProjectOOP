package com.viettel.backend.engine.cache;

import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;

public class ListCache<K, V> extends Cache<K> {
    
    public ListCache(String name, byte[] prefix, RedisTemplate<? extends Object, ? extends Object> template) {
        super(name, prefix, template);
    }

    @SuppressWarnings("unchecked")
    public BoundListOperations<K, V> get(K key) {
        return template.boundListOps(key);
    }
    
}
