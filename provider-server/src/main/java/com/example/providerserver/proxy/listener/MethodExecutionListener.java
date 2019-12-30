package com.example.providerserver.proxy.listener;

/**
 * Callback listener for JDBC API method invocations.
 *
 * @author complone
 * @since 1.4.3
 */
public interface MethodExecutionListener {

    MethodExecutionListener DEFAULT = new NoOpMethodExecutionListener();

    void beforeMethod(MethodExecutionContext executionContext);

    void afterMethod(MethodExecutionContext executionContext);

}
