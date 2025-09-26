package com.sdlc.pro.txboard.util;

import java.lang.reflect.InvocationTargetException;

public final class ObjectMapperUtils {

    public static String mapAsJsonString(Object objectMapper, Object object) {
        try {
            return (String) objectMapper.getClass().getMethod("writeValueAsString", Object.class)
                    .invoke(objectMapper, object);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
