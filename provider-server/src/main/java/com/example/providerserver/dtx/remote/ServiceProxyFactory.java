/*
 * Copyright (c) 2001-2019 GuaHao.com Corporation Limited. All rights reserved.
 * This software is the confidential and proprietary information of GuaHao Company.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with GuaHao.com.
 */
package com.example.providerserver.dtx.remote;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 后台业务的代理工厂
 * @author Administrator
 *
 */
public class ServiceProxyFactory {
    private static Map<Class<?>, Object> proxies = new ConcurrentHashMap<>();
    /**
     * 获取代理对象
     * 实例1： ReportDataStatService reportDataStatService = ServiceProxyFactory.get(ReportDataStatService.class);
     * @param intelClass
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> intelClass){
        if(proxies.containsKey(intelClass)){
            return (T)proxies.get(intelClass);
        }
        RemoteServiceInvokeHandler handler
            = new RemoteServiceInvokeHandler(intelClass);
        Object proxy = Proxy.newProxyInstance(intelClass.getClassLoader(), new Class[]{intelClass}, handler);
        proxies.put(intelClass, proxy);
        return (T)proxy;
    }

    public static <T> T get(Class<T> intelClass,String serviceFileName){
        if(proxies.containsKey(intelClass)){
            return (T)proxies.get(intelClass);
        }
        RemoteServiceInvokeHandler handler;
        if(serviceFileName==null) {
            handler = new RemoteServiceInvokeHandler(intelClass);
        }else{
            handler = new RemoteServiceInvokeHandler(intelClass,serviceFileName);
        }
        Object proxy = Proxy.newProxyInstance(intelClass.getClassLoader(), new Class[]{intelClass}, handler);
        proxies.put(intelClass, proxy);
        return (T)proxy;
    }
}

