package com.example.providerserver.proxy.support;

import com.example.providerserver.proxy.ConnectionIdManager;
import com.example.providerserver.proxy.ExecutionInfo;
import com.example.providerserver.proxy.QueryInfo;
import com.example.providerserver.proxy.listener.DataSourceQueryCountListener;
import com.example.providerserver.proxy.listener.MethodExecutionContext;
import com.example.providerserver.proxy.listener.MethodExecutionListener;
import com.example.providerserver.proxy.listener.NoOpMethodExecutionListener;
import com.example.providerserver.proxy.listener.NoOpQueryExecutionListener;
import com.example.providerserver.proxy.listener.QueryCountStrategy;
import com.example.providerserver.proxy.listener.QueryExecutionListener;
import com.example.providerserver.proxy.listener.TracingMethodListener;
import com.example.providerserver.proxy.proxy.DefaultConnectionIdManager;
import com.example.providerserver.proxy.proxy.JdbcProxyFactory;
import com.example.providerserver.proxy.proxy.ProxyConfig;
import com.example.providerserver.proxy.transform.ParameterTransformer;
import com.example.providerserver.proxy.transform.QueryTransformer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import javax.sql.DataSource;



/**
 * Builder for
 *
 * @author complone
 * @since 1.3
 */
public class ProxyDataSourceBuilder {

    /**
     * Functional interface to simplify adding {@link MethodExecutionListener}.
     *
     * @see #beforeMethod(SingleMethodExecution)
     * @see #afterMethod(SingleMethodExecution)
     * @since 1.4.3
     */
    // TODO: add @FunctionalInterface once codebase is java8
    public interface SingleMethodExecution {
        void execute(MethodExecutionContext executionContext);
    }

    /**
     * Functional interface to simplify adding {@link QueryExecutionListener}.
     *
     * @see #beforeQuery(SingleQueryExecution)
     * @see #afterQuery(SingleQueryExecution)
     * @since 1.4.3
     */
    // TODO: add @FunctionalInterface once codebase is java8
    public interface SingleQueryExecution {
        void execute(ExecutionInfo execInfo, List<QueryInfo> queryInfoList);
    }

    private DataSource dataSource;
    private String dataSourceName;

    // For building TracingMethodListener
    private boolean createTracingMethodListener;
    private TracingMethodListener.TracingCondition tracingCondition;
    private TracingMethodListener.TracingMessageConsumer tracingMessageConsumer;

    // For building QueryLoggingListeners



    // for JULQueryLoggingListener
    private boolean createJulQueryListener;
    private Level julLogLevel;
    private String julLoggerName;

    // for SystemOutQueryLoggingListener
    private boolean createSysOutQueryListener;


    // For building  SlowQueryListeners

    private long slowQueryThreshold;
    private TimeUnit slowQueryTimeUnit;



    // for JULSlowQueryListener
    private boolean createJulSlowQueryListener;
    private Level julSlowQueryLogLevel;
    private String julSlowQueryLoggerName;

    // for SystemOutSlowQueryListener
    private boolean createSysOutSlowQueryListener;

    private boolean createDataSourceQueryCountListener;
    private QueryCountStrategy queryCountStrategy;

    private boolean jsonFormat;
    private boolean multiline;
    private List<QueryExecutionListener> queryExecutionListeners = new ArrayList<QueryExecutionListener>();

    private ParameterTransformer parameterTransformer;
    private QueryTransformer queryTransformer;

    private JdbcProxyFactory jdbcProxyFactory;
    private ConnectionIdManager connectionIdManager;


    private boolean autoRetrieveGeneratedKeys;
    private Boolean retrieveGeneratedKeysForBatchStatement;
    private Boolean retrieveGeneratedKeysForBatchPreparedOrCallable;
    private boolean autoCloseGeneratedKeys;

    private List<MethodExecutionListener> methodExecutionListeners = new ArrayList<MethodExecutionListener>();

    public static ProxyDataSourceBuilder create() {
        return new ProxyDataSourceBuilder();
    }

    public static ProxyDataSourceBuilder create(DataSource dataSource) {
        return new ProxyDataSourceBuilder(dataSource);
    }

    public static ProxyDataSourceBuilder create(String dataSourceName, DataSource dataSource) {
        return new ProxyDataSourceBuilder(dataSource).name(dataSourceName);
    }

    public ProxyDataSourceBuilder() {
    }

    public ProxyDataSourceBuilder(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Set actual datasource.
     *
     * @param dataSource actual datasource
     * @return builder
     */
    public ProxyDataSourceBuilder dataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }



    /**
     *
     * @return builder
     */
    public ProxyDataSourceBuilder countQuery() {
        this.createDataSourceQueryCountListener = true;
        return this;
    }

    /**
     *
     * @return builder
     * @since 1.4.2
     */
    public ProxyDataSourceBuilder countQuery(QueryCountStrategy queryCountStrategy) {
        this.createDataSourceQueryCountListener = true;
        this.queryCountStrategy = queryCountStrategy;
        return this;
    }

    /**
     * Register given listener.
     *
     * @param listener a listener to register
     * @return builder
     */
    public ProxyDataSourceBuilder listener(QueryExecutionListener listener) {
        this.queryExecutionListeners.add(listener);
        return this;
    }


    /**
     * Add {@link QueryExecutionListener} that performs given lambda on {@link QueryExecutionListener#beforeQuery(ExecutionInfo, java.util.List)}.
     *
     * @param callback a lambda function executed on {@link QueryExecutionListener#beforeQuery(ExecutionInfo, java.util.List)}
     * @return builder
     * @since 1.4.3
     */
    public ProxyDataSourceBuilder beforeQuery(final SingleQueryExecution callback) {
        QueryExecutionListener listener = new NoOpQueryExecutionListener() {
            @Override
            public void beforeQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
                callback.execute(execInfo, queryInfoList);
            }
        };
        this.queryExecutionListeners.add(listener);
        return this;
    }

    /**
     * @param callback a lambda function executed
     * @return builder
     * @since 1.4.3
     */
    public ProxyDataSourceBuilder afterQuery(final SingleQueryExecution callback) {
        QueryExecutionListener listener = new NoOpQueryExecutionListener() {
            @Override
            public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
                callback.execute(execInfo, queryInfoList);
            }
        };
        this.queryExecutionListeners.add(listener);
        return this;
    }

    /**
     * Format logging output as JSON.
     *
     * @return builder
     */
    public ProxyDataSourceBuilder asJson() {
        this.jsonFormat = true;
        return this;
    }

    /**
     * Set datasource name.
     *
     * @param dataSourceName datasource name
     * @return builder
     */
    public ProxyDataSourceBuilder name(String dataSourceName) {
        this.dataSourceName = dataSourceName;
        return this;
    }

    /**
     *
     * @param queryTransformer a query-transformer to register
     * @return builder
     */
    public ProxyDataSourceBuilder queryTransformer(QueryTransformer queryTransformer) {
        this.queryTransformer = queryTransformer;
        return this;
    }

    /**
     *
     * @param parameterTransformer a query-parameter-transformer to register
     * @return builder
     */
    public ProxyDataSourceBuilder parameterTransformer(ParameterTransformer parameterTransformer) {
        this.parameterTransformer = parameterTransformer;
        return this;
    }

    /**
     * Use multiline output for logging query.
     *
     * @return builder
     * @since 1.4.1
     */
    public ProxyDataSourceBuilder multiline() {
        this.multiline = true;
        return this;
    }

    /**
     * Register {@link JdbcProxyFactory}.
     *
     * @param jdbcProxyFactory a JdbcProxyFactory to register
     * @return builder
     * @since 1.4.2
     */
    public ProxyDataSourceBuilder jdbcProxyFactory(JdbcProxyFactory jdbcProxyFactory) {
        this.jdbcProxyFactory = jdbcProxyFactory;
        return this;
    }

    /**
     * Register {@link ConnectionIdManager}.
     *
     * @param connectionIdManager a ConnectionIdManager to register
     * @return builder
     * @since 1.4.2
     */
    public ProxyDataSourceBuilder connectionIdManager(ConnectionIdManager connectionIdManager) {
        this.connectionIdManager = connectionIdManager;
        return this;
    }




    /**
     * Enable auto retrieval of generated keys.
     *
     * When it is enabled, after executing query, it always call {@link java.sql.Statement#getGeneratedKeys()}.
     * The retrieved {@link java.sql.ResultSet} is available via {@link ExecutionInfo#getGeneratedKeys()}.
     *
     * When this configuration is combined with  the proxied
     * {@link java.sql.ResultSet} will be returned from {@link ExecutionInfo#getGeneratedKeys()}.
     *
     * When autoClose parameter is set to {@code true}, datasource-proxy will close the generated-keys {@link java.sql.ResultSet}
     * after it called
     * This behavior might not be ideal if above layer, such as OR Mapper or application code, need to access generated-keys
     * because when they access generated-keys, the resultset is already closed.
     *
     * To support such usecase, specifyand set {@code autoClose=false}.
     * This way, even though your {@link QueryExecutionListener} has accessed generated-keys, it is still readable at
     * upper layer of the code, and they can close the generated-keys resultset.
     *
     * @param autoClose set {@code true} to close the generated-keys {@link java.sql.ResultSet} after query listener execution
     * @return builder
     * @since 1.4.5
     */
    public ProxyDataSourceBuilder autoRetrieveGeneratedKeys(boolean autoClose) {
        this.autoRetrieveGeneratedKeys = true;
        this.autoCloseGeneratedKeys = autoClose;
        return this;
    }

    /**
     * Enable auto retrieval of generated keys with proxy created by specified factory.
     *
     * See detail on {@link #autoRetrieveGeneratedKeys(boolean)}.
     *
     * @param autoClose set {@code true} to close the generated-keys {@link java.sql.ResultSet} after query listener execution
     * @param factory   a factory to create a generated-keys proxy
     * @return builder
     * @since 1.4.5
     */

    /**
     * Configure generated-keys retrieval for batch statement and prepared/callable when auto retrieval is enabled.
     *
     * Since JDBC spec defines creation of generated-keys for batch executions are driver implementation specific,
     * this method controls whether to auto-retrieve generated-keys for batch execution of {@link java.sql.Statement} and
     * {@link java.sql.PreparedStatement} / {@link java.sql.CallableStatement}.
     * Setting is only effective when generated-keys auto-retrieval is enabled.
     *
     * Defult values are set {@code false} for {@link java.sql.Statement}, {@code true} for {@link java.sql.PreparedStatement}
     * and {@link java.sql.CallableStatement}.
     *
     * @param forStatement          for {@link java.sql.Statement}
     * @param forPreparedOrCallable for {@link java.sql.PreparedStatement} and {@link java.sql.CallableStatement}
     * @return builder
     * @since 1.4.6
     */
    public ProxyDataSourceBuilder retrieveGeneratedKeysForBatch(boolean forStatement, boolean forPreparedOrCallable) {
        this.retrieveGeneratedKeysForBatchStatement = forStatement;
        this.retrieveGeneratedKeysForBatchPreparedOrCallable = forPreparedOrCallable;
        return this;
    }




    /**
     * Add {@link MethodExecutionListener}.
     *
     * @param listener a method execution listener
     * @return builder
     * @since 1.4.3
     */
    public ProxyDataSourceBuilder methodListener(MethodExecutionListener listener) {
        this.methodExecutionListeners.add(listener);
        return this;
    }

    /**
     * Add {@link MethodExecutionListener} that performs given lambda on {@link MethodExecutionListener#beforeMethod(MethodExecutionContext)}.
     *
     * @param callback a lambda function executed on {@link MethodExecutionListener#beforeMethod(MethodExecutionContext)}
     * @return builder
     * @since 1.4.3
     */
    public ProxyDataSourceBuilder beforeMethod(final SingleMethodExecution callback) {
        MethodExecutionListener listener = new NoOpMethodExecutionListener() {
            @Override
            public void beforeMethod(MethodExecutionContext executionContext) {
                callback.execute(executionContext);
            }
        };
        this.methodExecutionListeners.add(listener);
        return this;
    }

    /**
     * Add {@link MethodExecutionListener} that performs given lambda on {@link MethodExecutionListener#afterMethod(MethodExecutionContext)}.
     *
     * @param callback a lambda function executed on {@link MethodExecutionListener#afterMethod(MethodExecutionContext)}
     * @return builder
     * @since 1.4.3
     */
    public ProxyDataSourceBuilder afterMethod(final SingleMethodExecution callback) {
        MethodExecutionListener listener = new NoOpMethodExecutionListener() {
            @Override
            public void afterMethod(MethodExecutionContext executionContext) {
                callback.execute(executionContext);
            }
        };
        this.methodExecutionListeners.add(listener);
        return this;
    }

    /**
     * Enable {@link TracingMethodListener}.
     *
     * @return builder
     * @since 1.4.4
     */
    public ProxyDataSourceBuilder traceMethods() {
        this.createTracingMethodListener = true;
        return this;
    }

    /**
     * Enable {@link TracingMethodListener} with consumer that receives trace logging message.
     *
     * @param messageConsumer receives trace logging message
     * @return builder
     * @since 1.4.4
     */
    public ProxyDataSourceBuilder traceMethods(TracingMethodListener.TracingMessageConsumer messageConsumer) {
        this.createTracingMethodListener = true;
        this.tracingMessageConsumer = messageConsumer;
        return this;
    }

    /**
     * Enable {@link TracingMethodListener}.
     *
     * When given condition returns {@code true}, it prints out trace log.
     * The condition is used for dynamically turn on/off tracing.
     *
     * @param condition decide to turn on/off tracing
     * @return builder
     * @since 1.4.4
     */
    public ProxyDataSourceBuilder traceMethodsWhen(TracingMethodListener.TracingCondition condition) {
        this.createTracingMethodListener = true;
        this.tracingCondition = condition;
        return this;
    }

    /**
     * Enable {@link TracingMethodListener}.
     *
     * When given condition returns {@code true}, it prints out trace log.
     * The condition is used for dynamically turn on/off tracing.
     * The message consumer receives a tracing message that can be printed to console, logger, etc.
     *
     * @param condition       decide to turn on/off tracing
     * @param messageConsumer receives trace logging message
     * @return builder
     * @since 1.4.4
     */
    public ProxyDataSourceBuilder traceMethodsWhen(TracingMethodListener.TracingCondition condition, TracingMethodListener.TracingMessageConsumer messageConsumer) {
        this.createTracingMethodListener = true;
        this.tracingCondition = condition;
        this.tracingMessageConsumer = messageConsumer;
        return this;
    }


    public ProxyDataSource build() {

        // Query Logging Listeners
        List<QueryExecutionListener> listeners = new ArrayList<QueryExecutionListener>();

        // countQuery listener
        if (this.createDataSourceQueryCountListener) {
            DataSourceQueryCountListener countListener = new DataSourceQueryCountListener();

            if (this.queryCountStrategy != null) {
                countListener.setQueryCountStrategy(this.queryCountStrategy);
            }

            listeners.add(countListener);
        }

        // tracing listener
        if (this.createTracingMethodListener) {
            this.methodExecutionListeners.add(buildTracingMethodListenr());
        }

        // explicitly added listeners
        listeners.addAll(this.queryExecutionListeners);


        // build proxy config
        ProxyConfig.Builder proxyConfigBuilder = ProxyConfig.Builder.create();

        for (QueryExecutionListener listener : listeners) {
            proxyConfigBuilder.queryListener(listener);
        }

        for (MethodExecutionListener methodListener : this.methodExecutionListeners) {
            proxyConfigBuilder.methodListener(methodListener);
        }

        if (this.queryTransformer != null) {
            proxyConfigBuilder.queryTransformer(this.queryTransformer);
        }
        if (this.parameterTransformer != null) {
            proxyConfigBuilder.parameterTransformer(this.parameterTransformer);
        }


        // DataSource Name
        if (this.dataSourceName != null) {
            proxyConfigBuilder.dataSourceName(dataSourceName);
        }


        if (this.jdbcProxyFactory != null) {
            proxyConfigBuilder.jdbcProxyFactory(this.jdbcProxyFactory);
        } else {
            proxyConfigBuilder.jdbcProxyFactory(JdbcProxyFactory.DEFAULT);

        }

        if (this.connectionIdManager != null) {
            proxyConfigBuilder.connectionIdManager(this.connectionIdManager);
        } else {
            proxyConfigBuilder.connectionIdManager(new DefaultConnectionIdManager());
        }

        // generated keys
        proxyConfigBuilder.autoRetrieveGeneratedKeys(this.autoRetrieveGeneratedKeys);
        if (this.retrieveGeneratedKeysForBatchStatement != null) {
            proxyConfigBuilder.retrieveGeneratedKeysForBatchStatement(this.retrieveGeneratedKeysForBatchStatement);
        }
        if (this.retrieveGeneratedKeysForBatchPreparedOrCallable != null) {
            proxyConfigBuilder.retrieveGeneratedKeysForBatchPreparedOrCallable(this.retrieveGeneratedKeysForBatchPreparedOrCallable);
        }
        proxyConfigBuilder.autoCloseGeneratedKeys(this.autoCloseGeneratedKeys);
        // build ProxyDataSource
        ProxyDataSource proxyDataSource = new ProxyDataSource();
        if (this.dataSource != null) {
            proxyDataSource.setDataSource(dataSource);
        }
        ProxyConfig proxyConfig = proxyConfigBuilder.build();
        proxyDataSource.setProxyConfig(proxyConfig);

        return proxyDataSource;
    }


    private TracingMethodListener buildTracingMethodListenr() {
        TracingMethodListener listener = new TracingMethodListener();
        if (this.tracingMessageConsumer != null) {
            listener.setTracingMessageConsumer(this.tracingMessageConsumer);
        }
        if (this.tracingCondition != null) {
            listener.setTracingCondition(this.tracingCondition);
        }
        return listener;
    }

}
