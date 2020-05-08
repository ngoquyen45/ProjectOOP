package com.viettel.backend.engine.cache;

import java.util.Arrays;

import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;

public class PrefixSupportJdkSerializationRedisSerializer extends JdkSerializationRedisSerializer {

	private byte[] prefix;
	
	public PrefixSupportJdkSerializationRedisSerializer(byte[] prefix) {
		this.prefix = prefix;
	}
	
	@Override
	public byte[] serialize(Object object) {
		byte[] rawKey = super.serialize(object);
		if (!hasPrefix()) {
			return rawKey;
		}

		byte[] prefixedKey = Arrays.copyOf(prefix, prefix.length + rawKey.length);
		System.arraycopy(rawKey, 0, prefixedKey, prefix.length, rawKey.length);

		return prefixedKey;
	}
	
	@Override
	public Object deserialize(byte[] bytes) {
		if (!hasPrefix()) {
			return super.deserialize(bytes);
		}
		byte[] rawKey = Arrays.copyOfRange(bytes, prefix.length, bytes.length - 1);
		return super.deserialize(rawKey);
	}
	
	/**
	 * @return true if prefix is not empty.
	 */
	public boolean hasPrefix() {
		return (prefix != null && prefix.length > 0);
	}
}
