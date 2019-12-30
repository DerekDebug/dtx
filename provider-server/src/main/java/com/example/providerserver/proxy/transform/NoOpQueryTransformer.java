package com.example.providerserver.proxy.transform;

/**
 * No operation implementation of {@link QueryTransformer}.
 *
 * @author complone
 * @since 1.2
 */
public class NoOpQueryTransformer implements QueryTransformer {
    public String transformQuery(TransformInfo transformInfo) {
        return transformInfo.getQuery();
    }
}
