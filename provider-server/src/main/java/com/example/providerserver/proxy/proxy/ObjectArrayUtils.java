package com.example.providerserver.proxy.proxy;

/**
 * @author complone
 */
public class ObjectArrayUtils {
    public static boolean isFirstArgString(Object[] args) {
        return args != null && args.length >= 1 && args[0] instanceof String;
    }

}
