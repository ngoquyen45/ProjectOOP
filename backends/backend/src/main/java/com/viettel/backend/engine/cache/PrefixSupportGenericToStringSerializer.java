package com.viettel.backend.engine.cache;

import java.nio.charset.Charset;
import java.util.Arrays;

import org.springframework.data.redis.serializer.GenericToStringSerializer;

public class PrefixSupportGenericToStringSerializer<T> extends GenericToStringSerializer<T> {
	
	private byte[] prefix;
	
	public PrefixSupportGenericToStringSerializer(byte[] prefix, Class<T> type) {
		super(type);
		this.prefix = prefix;
	}

	public PrefixSupportGenericToStringSerializer(byte[] prefix, Class<T> type, Charset charset) {
		super(type, charset);
		this.prefix = prefix;
	}
	
	@Override
	public byte[] serialize(T object) {
		byte[] rawKey = super.serialize(object);
		if (!hasPrefix()) {
			return rawKey;
		}

		byte[] prefixedKey = Arrays.copyOf(prefix, prefix.length + rawKey.length);
		System.arraycopy(rawKey, 0, prefixedKey, prefix.length, rawKey.length);

		return prefixedKey;
	}
	
	@Override
	public T deserialize(byte[] bytes) {
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
