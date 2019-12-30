package com.example.providerserver.proxy.proxy;

import com.example.providerserver.proxy.ConnectionInfo;
import com.example.providerserver.proxy.listener.MethodExecutionListenerUtils;
import com.example.providerserver.proxy.transform.QueryTransformer;
import com.example.providerserver.proxy.transform.TransformInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;



/**
 * Proxy Logic implementation for {@link java.sql.Connection} methods.
 *
 * @author complone
 * @since 1.2
 */
public class ConnectionProxyLogic {

    private static final Set<String> JDBC4_METHODS = Collections.unmodifiableSet(
            new HashSet<String>(Arrays.asList("unwrap", "isWrapperFor"))
    );

    private Connection connection;
    private ConnectionInfo connectionInfo;
    private ProxyConfig proxyConfig;

    public ConnectionProxyLogic(Connection connection, ConnectionInfo connectionInfo, ProxyConfig proxyConfig) {
        this.connection = connection;
        this.connectionInfo = connectionInfo;
        this.proxyConfig = proxyConfig;
    }

    public Object invoke(final Object proxyConnection, Method method, Object[] args) throws Throwable {

        final boolean isCloseMethod = "close".equals(method.getName());
        final boolean isCommitMethod = "commit".equals(method.getName());
        final boolean isRollbackMethod = "rollback".equals(method.getName());

        return MethodExecutionListenerUtils.invoke(new MethodExecutionListenerUtils.MethodExecutionCallback() {
            @Override
            public Object execute(Object proxyTarget, Method method, Object[] args) throws Throwable {
                Object result = performQueryExecutionListener(proxyConnection, method, args);
                ConnectionInfo connectionInfo = ConnectionProxyLogic.this.connectionInfo;
                if (isCommitMethod) {
                    connectionInfo.incrementCommitCount();
                } else if (isRollbackMethod) {
                    connectionInfo.incrementRollbackCount();
                } else if (isCloseMethod) {
                    connectionInfo.setClosed(true);
                    String connId = connectionInfo.getConnectionId();
                    ConnectionProxyLogic.this.proxyConfig.getConnectionIdManager().addClosedId(connId);
                }
                return result;
            }
        }, this.proxyConfig, this.connection, this.connectionInfo, method, args);
    }

    private Object performQueryExecutionListener(Object proxy, Method method, Object[] args) throws Throwable {
        final Connection proxyConnection = (Connection) proxy;
        final String methodName = method.getName();

        QueryTransformer queryTransformer = this.proxyConfig.getQueryTransformer();
        JdbcProxyFactory jdbcProxyFactory = this.proxyConfig.getJdbcProxyFactory();


        if ("toString".equals(methodName)) {
            final StringBuilder sb = new StringBuilder();
            sb.append(this.connection.getClass().getSimpleName());
            sb.append(" [");
            sb.append(this.connection.toString());
            sb.append("]");
            return sb.toString(); // differentiate toString message.
        } else if ("getDataSourceName".equals(methodName)) {
            return this.connectionInfo.getDataSourceName();
        } else if ("getTarget".equals(methodName)) {
            // ProxyJdbcObject interface has method to return original object.
            return this.connection;
        }

        if (JDBC4_METHODS.contains(methodName)) {
            final Class<?> clazz = (Class<?>) args[0];
            if ("unwrap".equals(methodName)) {
                return this.connection.unwrap(clazz);
            } else if ("isWrapperFor".equals(methodName)) {
                return this.connection.isWrapperFor(clazz);
            }
        }

        // replace query for PreparedStatement and CallableStatement
        if ("prepareStatement".equals(methodName) || "prepareCall".equals(methodName)) {
            if (ObjectArrayUtils.isFirstArgString(args)) {
                final String query = (String) args[0];
                final Class<? extends Statement> clazz =
                        "prepareStatement".equals(methodName) ? PreparedStatement.class : CallableStatement.class;
                final TransformInfo transformInfo = new TransformInfo(clazz, this.connectionInfo.getDataSourceName(), query, false, 0);
                final String transformedQuery = queryTransformer.transformQuery(transformInfo);
                args[0] = transformedQuery;
            }
        }

        // Invoke method on original Connection.
        final Object retVal;
        try {
            retVal = method.invoke(this.connection, args);
        } catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        }


        return retVal;
    }

}
