package com.viettel.backend.engine.cache;

import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;

public class HashCache<H, HK, HV> extends Cache<H> {
    
    public HashCache(String name, byte[] prefix, RedisTemplate<? extends Object, ? extends Object> template) {
        super(name, prefix, template);
    }

    @SuppressWarnings("unchecked")
    public <T> BoundHashOperations<H, HK, HV> get(H key) {
        return template.boundHashOps(key);
    }

}
