package com.example.providerserver.proxy.proxy;

import com.example.providerserver.proxy.ConnectionInfo;
import com.example.providerserver.proxy.proxy.jdk.JdkJdbcProxyFactory;

import java.sql.Connection;

import javax.sql.DataSource;


/**
 * Factory interface to return a proxy with InvocationHandler used by datasource-proxy.
 *
 * @author complone
 */
public interface JdbcProxyFactory {

    /**
     * use JDK proxy as default.
     */
    JdbcProxyFactory DEFAULT = new JdkJdbcProxyFactory();


    DataSource createDataSource(DataSource dataSource, ProxyConfig proxyConfig);

    Connection createConnection(Connection connection, ConnectionInfo connectionInfo, ProxyConfig proxyConfig);


}