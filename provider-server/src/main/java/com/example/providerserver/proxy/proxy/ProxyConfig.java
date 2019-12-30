package com.example.providerserver.proxy.proxy;

import com.example.providerserver.proxy.ConnectionIdManager;
import com.example.providerserver.proxy.listener.ChainListener;
import com.example.providerserver.proxy.listener.CompositeMethodListener;
import com.example.providerserver.proxy.listener.MethodExecutionListener;
import com.example.providerserver.proxy.listener.QueryExecutionListener;
import com.example.providerserver.proxy.transform.ParameterTransformer;
import com.example.providerserver.proxy.transform.QueryTransformer;

/**
 * Hold configuration objects for creating a proxy.
 *
 * @author complone
 * @since 1.4.3
 */
public class ProxyConfig {

    private static class GeneratedKeysConfig {

        private boolean autoRetrieve;
        private boolean retrieveForBatchStatement = false;  // default false
        private boolean retrieveForBatchPreparedOrCallable = true;  // default true
        private boolean autoClose;
    }

    public static class Builder {
        private String dataSourceName = "";
        private ChainListener queryListener = new ChainListener();  // empty default
        private QueryTransformer queryTransformer = QueryTransformer.DEFAULT;
        private ParameterTransformer parameterTransformer = ParameterTransformer.DEFAULT;
        private JdbcProxyFactory jdbcProxyFactory = JdbcProxyFactory.DEFAULT;
        private ConnectionIdManager connectionIdManager = new DefaultConnectionIdManager();  // create instance every time
        private CompositeMethodListener methodListener = new CompositeMethodListener();  // empty default
        private GeneratedKeysConfig generatedKeysConfig = new GeneratedKeysConfig();

        public static Builder create() {
            return new Builder();
        }

        public static Builder from(ProxyConfig proxyConfig) {
            return new Builder()
                    .dataSourceName(proxyConfig.dataSourceName)
                    .queryListener(proxyConfig.queryListener)
                    .queryTransformer(proxyConfig.queryTransformer)
                    .parameterTransformer(proxyConfig.parameterTransformer)
                    .jdbcProxyFactory(proxyConfig.jdbcProxyFactory)
                    .connectionIdManager(proxyConfig.connectionIdManager)
                    .methodListener(proxyConfig.methodListener)
                    .autoRetrieveGeneratedKeys(proxyConfig.generatedKeysConfig.autoRetrieve)
                    .retrieveGeneratedKeysForBatchStatement(proxyConfig.generatedKeysConfig.retrieveForBatchStatement)
                    .retrieveGeneratedKeysForBatchPreparedOrCallable(proxyConfig.generatedKeysConfig.retrieveForBatchPreparedOrCallable)
                    .autoCloseGeneratedKeys(proxyConfig.generatedKeysConfig.autoClose)
                    ;
        }

        public ProxyConfig build() {
            ProxyConfig proxyConfig = new ProxyConfig();
            proxyConfig.dataSourceName = this.dataSourceName;
            proxyConfig.queryListener = this.queryListener;
            proxyConfig.queryTransformer = this.queryTransformer;
            proxyConfig.parameterTransformer = this.parameterTransformer;
            proxyConfig.jdbcProxyFactory = this.jdbcProxyFactory;
            proxyConfig.connectionIdManager = this.connectionIdManager;
            proxyConfig.methodListener = this.methodListener;


            return proxyConfig;
        }

        public Builder dataSourceName(String dataSourceName) {
            this.dataSourceName = dataSourceName;
            return this;
        }

        public Builder queryListener(QueryExecutionListener queryListener) {
            if (queryListener instanceof ChainListener) {
                for (QueryExecutionListener listener : ((ChainListener) queryListener).getListeners()) {
                    this.queryListener.addListener(listener);
                }
            } else {
                this.queryListener.addListener(queryListener);
            }
            return this;
        }

        public Builder queryTransformer(QueryTransformer queryTransformer) {
            this.queryTransformer = queryTransformer;
            return this;
        }

        public Builder parameterTransformer(ParameterTransformer parameterTransformer) {
            this.parameterTransformer = parameterTransformer;
            return this;
        }

        public Builder jdbcProxyFactory(JdbcProxyFactory jdbcProxyFactory) {
            this.jdbcProxyFactory = jdbcProxyFactory;
            return this;
        }


        public Builder autoRetrieveGeneratedKeys(boolean autoRetrieveGeneratedKeys) {
            this.generatedKeysConfig.autoRetrieve = autoRetrieveGeneratedKeys;
            return this;
        }

        public Builder autoCloseGeneratedKeys(boolean autoCloseGeneratedKeys) {
            this.generatedKeysConfig.autoClose = autoCloseGeneratedKeys;
            return this;
        }

        public Builder retrieveGeneratedKeysForBatchStatement(boolean retrieveForBatchStatement) {
            this.generatedKeysConfig.retrieveForBatchStatement = retrieveForBatchStatement;
            return this;
        }

        public Builder retrieveGeneratedKeysForBatchPreparedOrCallable(boolean retrieveForBatchPreparedOrCallable) {
            this.generatedKeysConfig.retrieveForBatchPreparedOrCallable = retrieveForBatchPreparedOrCallable;
            return this;
        }

        public Builder connectionIdManager(ConnectionIdManager connectionIdManager) {
            this.connectionIdManager = connectionIdManager;
            return this;
        }

        public Builder methodListener(MethodExecutionListener methodListener) {
            if (methodListener instanceof CompositeMethodListener) {
                for (MethodExecutionListener listener : ((CompositeMethodListener) methodListener).getListeners()) {
                    this.methodListener.addListener(listener);
                }
            } else {
                this.methodListener.addListener(methodListener);
            }
            return this;
        }


    }

    private String dataSourceName;
    private ChainListener queryListener;
    private QueryTransformer queryTransformer;
    private ParameterTransformer parameterTransformer;
    private JdbcProxyFactory jdbcProxyFactory;

    private ConnectionIdManager connectionIdManager;
    private CompositeMethodListener methodListener;
    private GeneratedKeysConfig generatedKeysConfig = new GeneratedKeysConfig();


    public String getDataSourceName() {
        return dataSourceName;
    }

    public ChainListener getQueryListener() {
        return queryListener;
    }

    public QueryTransformer getQueryTransformer() {
        return queryTransformer;
    }

    public ParameterTransformer getParameterTransformer() {
        return parameterTransformer;
    }

    public JdbcProxyFactory getJdbcProxyFactory() {
        return jdbcProxyFactory;
    }



    /**
     * Whether to auto close {@link java.sql.ResultSet} for generated-keys that is automatically retrieved.
     *
     * When this returns {@code true}, always close the {@link java.sql.ResultSet} for generated keys when
     * {@link QueryExecutionListener# has finished.
     * The result of {@link java.sql.Statement#getGeneratedKeys()} method will not be closed by this. Only auto retrieved
     * {@link java.sql.ResultSet} of generated keys is closed.
     *
     * @since 1.4.5
     */
    public boolean isAutoCloseGeneratedKeys() {
        return this.generatedKeysConfig.autoClose;
    }

    /**
     * Perform generated-keys auto retrieval for batch statement.
     *
     * Default is set to {@code false}.
     *
     * @since 1.4.6
     */
    public boolean isRetrieveGeneratedKeysForBatchStatement() {
        return this.generatedKeysConfig.retrieveForBatchStatement;
    }

    /**
     * Perform generated-keys auto retrieval for batch prepared or callable.
     *
     * Default is set to {@code true}.
     *
     * @since 1.4.6
     */
    public boolean isRetrieveGeneratedKeysForBatchPreparedOrCallable() {
        return this.generatedKeysConfig.retrieveForBatchPreparedOrCallable;
    }

    public ConnectionIdManager getConnectionIdManager() {
        return connectionIdManager;
    }

    public CompositeMethodListener getMethodListener() {
        return methodListener;
    }

}
