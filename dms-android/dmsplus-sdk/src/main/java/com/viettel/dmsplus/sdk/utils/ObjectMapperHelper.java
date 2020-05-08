package com.viettel.dmsplus.sdk.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @author ThanhNV60
 */
public class ObjectMapperHelper {
    private static ObjectMapper objectMapper;

    public static ObjectMapper singletonObjectMapper() {
        if (objectMapper == null) {
            synchronized (ObjectMapperHelper.class) {
                if (objectMapper == null) {
                    objectMapper = new ObjectMapper();
                    objectMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
                    objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
                }
            }
        }
        return objectMapper;
    }
    
    private ObjectMapperHelper() { };
}
