package com.example.providerserver.proxy.listener;

/**
 * No-op implementation of {@link MethodExecutionListener}
 *
 * @author complone
 * @since 1.4.3
 */
public class NoOpMethodExecutionListener implements MethodExecutionListener {

    @Override
    public void beforeMethod(MethodExecutionContext executionContext) {
        // no-op
    }

    @Override
    public void afterMethod(MethodExecutionContext executionContext) {
        // no-op
    }

}
