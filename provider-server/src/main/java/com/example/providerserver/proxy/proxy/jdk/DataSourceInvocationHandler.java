package com.example.providerserver.proxy.proxy.jdk;

import com.example.providerserver.proxy.proxy.DataSourceProxyLogic;
import com.example.providerserver.proxy.proxy.ProxyConfig;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import javax.sql.DataSource;


/**
 * Proxy InvocationHandler for {@link javax.sql.DataSource}.
 *
 * @author complone
 */
public class DataSourceInvocationHandler implements InvocationHandler {

    private DataSourceProxyLogic delegate;

    public DataSourceInvocationHandler(DataSource dataSource, ProxyConfig proxyConfig) {
        delegate = new DataSourceProxyLogic(dataSource, proxyConfig);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return delegate.invoke(method, args);
    }
}
