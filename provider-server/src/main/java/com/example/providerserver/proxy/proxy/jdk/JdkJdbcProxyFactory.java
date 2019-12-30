package com.example.providerserver.proxy.proxy.jdk;

import com.example.providerserver.proxy.ConnectionInfo;
import com.example.providerserver.proxy.proxy.JdbcProxyFactory;
import com.example.providerserver.proxy.proxy.ProxyConfig;
import com.example.providerserver.proxy.proxy.ProxyJdbcObject;

import java.lang.reflect.Proxy;
import java.sql.Connection;

import javax.sql.DataSource;



/**
 * Dynamic Proxy Class(Jdk Proxy)
 *
 * @author complone
 * @since 1.2
 */
public class JdkJdbcProxyFactory implements JdbcProxyFactory {

    @Override
    public DataSource createDataSource(DataSource dataSource, ProxyConfig proxyConfig) {
        return (DataSource) Proxy.newProxyInstance(ProxyJdbcObject.class.getClassLoader(),
                new Class[]{ProxyJdbcObject.class, DataSource.class},
                new DataSourceInvocationHandler(dataSource, proxyConfig));
    }

    @Override
    public Connection createConnection(Connection connection, ConnectionInfo connectionInfo, ProxyConfig proxyConfig) {
        return (Connection) Proxy.newProxyInstance(ProxyJdbcObject.class.getClassLoader(),
                new Class[]{ProxyJdbcObject.class, Connection.class},
                new ConnectionInvocationHandler(connection, connectionInfo, proxyConfig));
    }



}
