package com.viettel.backend.engine.cache;

import org.springframework.data.redis.cache.RedisCachePrefix;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.Assert;

/**
 * Append prefix a key with format "cache:<code>cache_name</code>:<code>cache_key</code>
 * @author thanh
 */
public class SimpleCachePrefix implements RedisCachePrefix {

    private final StringRedisSerializer serializer = new StringRedisSerializer();
    private final String delimiter;
    private final String prefix;

    public SimpleCachePrefix() {
        this("cache", ":");
    }

    public SimpleCachePrefix(String prefix, String delimiter) {
        this.prefix = prefix;
        this.delimiter = delimiter;
        
        Assert.hasText(prefix, "Prefix cannot be null");
        Assert.hasText(delimiter, "Delimiter cannot be null");
    }

    public byte[] prefix(String cacheName) {
        return serializer.serialize(new StringBuilder(prefix).append(delimiter).append(cacheName).append(delimiter).toString());
    }
    
    public byte[] prefixForAllCaches() {
        return serializer.serialize(new StringBuilder(prefix).append(delimiter).toString());
    }
    
}
