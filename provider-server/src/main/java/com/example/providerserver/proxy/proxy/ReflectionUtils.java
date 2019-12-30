package com.example.providerserver.proxy.proxy;

import java.lang.reflect.Method;

/**
 * @author complone
 * @since 1.4.6
 */
public class ReflectionUtils {

    public static Method getMethodIfAvailable(Class<?> clazz, String name, Class... parameterTypes) {
        try {
            return clazz.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

}
