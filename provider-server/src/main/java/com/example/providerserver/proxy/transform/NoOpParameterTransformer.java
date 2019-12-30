package com.example.providerserver.proxy.transform;

/**
 * No operation implementation of {@link ParameterTransformer}.
 *
 * @author complone
 * @since 1.2
 */
public class NoOpParameterTransformer implements ParameterTransformer {

    public void transformParameters(ParameterReplacer replacer, TransformInfo transformInfo) {
        // do nothing
    }
}
