package com.viettel.backend.engine.cache;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.viettel.backend.engine.cache.Cache.RedisCacheCleanByPrefixCallback;

/**
 * @author thanh
 */
public class CacheManager implements InitializingBean {

    private static final StringRedisSerializer STRING_REDIS_SERIALIZER = new StringRedisSerializer();
    
    @SuppressWarnings("rawtypes")
    private final ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<String, Cache>(16);
    private Set<String> cacheNames = new LinkedHashSet<String>(16);
    private RedisConnectionFactory connectionFactory;
    private SimpleCachePrefix cachePrefix = new SimpleCachePrefix();

    public CacheManager(RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }
    
    @Override
    public void afterPropertiesSet() {
        this.cacheMap.clear();
        this.cacheNames.clear();
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <K, V> ValueCache<K, V> getValueCache(String cacheName, Class<K> keyType, Class<V> valueType) {
        ValueCache cache = (ValueCache) lookupCache(cacheName);
        if (cache != null) {
            return cache;
        } else {
            byte[] prefix = cachePrefix.prefix(cacheName);
            RedisTemplate<K, V> template = constructTemplate(connectionFactory, keyType, valueType, prefix);
            cache = new ValueCache<K, V>(cacheName, prefix, template);
            addCache(cache);
            return (ValueCache<K, V>) lookupCache(cacheName);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <K, V> ListCache<K, V> getListCache(String cacheName, Class<K> keyType, Class<V> valueType) {
        ListCache cache = (ListCache) lookupCache(cacheName);
        if (cache != null) {
            return cache;
        } else {
            byte[] prefix = cachePrefix.prefix(cacheName);
            RedisTemplate<K, V> template = constructTemplate(connectionFactory, keyType, valueType, prefix);
            cache = new ListCache<K, V>(cacheName, prefix, template);
            addCache(cache);
            return (ListCache<K, V>) lookupCache(cacheName);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <K, V> SetCache<K, V> getSetCache(String cacheName, Class<K> keyType, Class<V> valueType) {
        SetCache cache = (SetCache) lookupCache(cacheName);
        if (cache != null) {
            return cache;
        } else {
            byte[] prefix = cachePrefix.prefix(cacheName);
            RedisTemplate<K, V> template = constructTemplate(connectionFactory, keyType, valueType, prefix);
            cache = new SetCache<K, V>(cacheName, prefix, template);
            addCache(cache);
            return (SetCache<K, V>) lookupCache(cacheName);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <K, V> ZSetCache<K, V> getZSetCache(String cacheName, Class<K> keyType, Class<V> valueType) {
        ZSetCache cache = (ZSetCache) lookupCache(cacheName);
        if (cache != null) {
            return cache;
        } else {
            byte[] prefix = cachePrefix.prefix(cacheName);
            RedisTemplate<K, V> template = constructTemplate(connectionFactory, keyType, valueType, prefix);
            cache = new ZSetCache<K, V>(cacheName, prefix, template);
            addCache(cache);
            return (ZSetCache<K, V>) lookupCache(cacheName);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <H, HK, HV> HashCache<H, HK, HV> getHashCache(String cacheName, Class<H> keyType,
            Class<HK> hashKeyType, Class<HV> hashValueType) {
        HashCache cache = (HashCache) lookupCache(cacheName);
        if (cache != null) {
            return cache;
        } else {
            byte[] prefix = cachePrefix.prefix(cacheName);
            RedisTemplate<H, Object> template = constructTemplate(connectionFactory, keyType, null, prefix);
            // BoundHashCache need additional initialization
            template.setHashKeySerializer(getSerializerForType(hashKeyType));
            template.setHashValueSerializer(getSerializerForType(hashValueType));
            cache = new HashCache<H, HK, HV>(cacheName, prefix, template);
            addCache(cache);
            return (HashCache<H, HK, HV>) lookupCache(cacheName);
        }
    }

    @SuppressWarnings("rawtypes")
    protected final void addCache(Cache cache) {
        this.cacheMap.put(cache.getName(), cache);
        this.cacheNames.add(cache.getName());
    }

    @SuppressWarnings("rawtypes")
    protected final Cache lookupCache(String name) {
        return this.cacheMap.get(name);
    }

    private <K, V> RedisTemplate<K, V> constructTemplate(RedisConnectionFactory connectionFactory, Class<K> keyType,
            Class<V> valueType, byte[] prefix) {
        RedisTemplate<K, V> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(getKeySerializerForType(keyType, prefix));
        if (valueType != null) {
            template.setValueSerializer(getSerializerForType(valueType));
        }
        template.afterPropertiesSet();
        return template;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private <T> RedisSerializer getKeySerializerForType(Class<T> type, byte[] prefix) {
        if (type.equals(String.class)) {
            return (RedisSerializer<T>) new PrefixSupportStringRedisSerializer(prefix);
        }
        if (isPrimitive(type)) {
            return new PrefixSupportGenericToStringSerializer<T>(prefix, type);
        }
        return new PrefixSupportJdkSerializationRedisSerializer(prefix);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private <T> RedisSerializer getSerializerForType(Class<T> type) {
        if (type.equals(String.class)) {
            return (RedisSerializer<T>) STRING_REDIS_SERIALIZER;
        }
        if (isPrimitive(type)) {
            return new GenericToStringSerializer<T>(type);
        }
        return new JdkSerializationRedisSerializer();
    }

    private static boolean isPrimitive(Class<?> type) {
        if (type.isPrimitive()) {
            return true;
        }
        if (Number.class.isAssignableFrom(type) || type == Boolean.class || type == Character.class) {
            return true;
        }
        return false;
    }
    
    /**
     * Clear all cache (determined by "cache:" prefix)
     */
    public synchronized void clearAll() {
        new StringRedisTemplate(connectionFactory)
                .execute(new RedisCacheCleanByPrefixCallback(cachePrefix.prefixForAllCaches()), true);
    }
    
}
