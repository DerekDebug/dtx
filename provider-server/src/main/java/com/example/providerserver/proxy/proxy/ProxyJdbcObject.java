package com.example.providerserver.proxy.proxy;

/**
 * Provide a method to unwrap the original jdbc object from proxy object.
 *
 * <p>Proxy object created by {@link JdbcProxyFactory} implements this interface.
 *
 * @author complone
 * @see JdbcProxyFactory
 */
public interface ProxyJdbcObject {

    /**
     * Method to return wrapped source object(Connection, Statement, PreparedStatement, CallableStatement).
     *
     * @return source object
     */
    Object getTarget();
}
