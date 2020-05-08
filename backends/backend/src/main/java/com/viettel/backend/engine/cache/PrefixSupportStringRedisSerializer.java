package com.viettel.backend.engine.cache;

import java.nio.charset.Charset;
import java.util.Arrays;

import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author thanh
 */
public class PrefixSupportStringRedisSerializer extends StringRedisSerializer {
	
	private byte[] prefix;

	public PrefixSupportStringRedisSerializer(byte[] prefix) {
		super();
		this.prefix = prefix;
	}

	public PrefixSupportStringRedisSerializer(byte[] prefix, Charset charset) {
		super(charset);
		this.prefix = prefix;
	}
	
	@Override
	public byte[] serialize(String string) {
		byte[] rawKey = super.serialize(string);
		if (!hasPrefix()) {
			return rawKey;
		}

		byte[] prefixedKey = Arrays.copyOf(prefix, prefix.length + rawKey.length);
		System.arraycopy(rawKey, 0, prefixedKey, prefix.length, rawKey.length);

		return prefixedKey;
	}
	
	@Override
	public String deserialize(byte[] bytes) {
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
