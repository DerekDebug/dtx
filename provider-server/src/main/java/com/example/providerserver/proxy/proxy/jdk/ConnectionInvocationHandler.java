package com.example.providerserver.proxy.proxy.jdk;

import com.example.providerserver.proxy.ConnectionInfo;
import com.example.providerserver.proxy.proxy.ConnectionProxyLogic;
import com.example.providerserver.proxy.proxy.ProxyConfig;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;


/**
 * Proxy InvocationHandler for {@link java.sql.Connection}.
 *
 * @author complone
 */
public class ConnectionInvocationHandler implements InvocationHandler {

    private ConnectionProxyLogic delegate;

    public ConnectionInvocationHandler(Connection connection, ConnectionInfo connectionInfo, ProxyConfig proxyConfig) {
        this.delegate = new ConnectionProxyLogic(connection, connectionInfo, proxyConfig);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return delegate.invoke(proxy, method, args);
    }

}
