package com.example.providerserver.proxy.transform;

/**
 * @author complone
 * @since 1.2
 */
public interface ParameterTransformer {

    static ParameterTransformer DEFAULT = new NoOpParameterTransformer();

    void transformParameters(ParameterReplacer replacer, TransformInfo transformInfo);
}
