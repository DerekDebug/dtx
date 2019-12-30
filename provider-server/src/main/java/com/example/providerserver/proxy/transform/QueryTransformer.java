package com.example.providerserver.proxy.transform;

/**
 * Interceptor that can transform the query statement.
 *
 * @author complone
 * @since 1.2
 */
public interface QueryTransformer {

    static QueryTransformer DEFAULT = new NoOpQueryTransformer();

    String transformQuery(TransformInfo transformInfo);
}
