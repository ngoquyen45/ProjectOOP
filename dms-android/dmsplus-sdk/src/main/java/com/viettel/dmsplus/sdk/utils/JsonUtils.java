package com.viettel.dmsplus.sdk.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JsonUtils {

    private static final String TAG = "JsonUtils";
    private static ObjectMapper objectMapper;

    public static ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            synchronized (JsonUtils.class) {
                if (objectMapper == null) {
                    objectMapper = new ObjectMapper();
                }
            }
        }
        return objectMapper;
    }

    public static String toJsonString(Object obj) {
        ObjectMapper objectMapper = getObjectMapper();
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            LogUtils.e(TAG, "Cannot convert object of class " + obj.getClass().getName() + " to JSON", e);
            return null;
        }
    }

    public static <T> T toObject(String json, Class<T> clazz) {
        ObjectMapper objectMapper = getObjectMapper();
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            LogUtils.e(TAG, "Cannot parse JSON to " + clazz.getName(), e);
            return null;
        }
    }

    public static <K, V> Map<K, V> toMap(String json, Class<K> keyType, Class<V> valueType) {
        ObjectMapper objectMapper = getObjectMapper();
        try {
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            MapType mapType = typeFactory.constructMapType(HashMap.class, keyType, valueType);
            return objectMapper.readValue(json, mapType);
        } catch (IOException e) {
            LogUtils.e(TAG, "Cannot parse JSON to Map<" + keyType.getName() + "," + valueType.getName() + ">", e);
            return null;
        }
    }
}
