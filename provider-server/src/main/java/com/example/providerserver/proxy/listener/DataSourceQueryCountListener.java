package com.example.providerserver.proxy.listener;

import com.example.providerserver.proxy.ExecutionInfo;
import com.example.providerserver.proxy.QueryCount;
import com.example.providerserver.proxy.QueryInfo;
import com.example.providerserver.proxy.QueryType;

import java.util.List;

/**
 * Update database access information.
 *
 * <p>Default implementation uses {@link ThreadQueryCountHolder} strategy that uses thread local to keep
 *
 * <p>Alternatively, {@link SingleQueryCountHolder} strategy can be used. This strategy uses single instance to keep
 * {@link QueryCount}; therefore, {@link QueryCount} holds accumulated total values from any threads until values are cleared.
 *
 * <p>In web application lifecycle, one http request is handled by one thread.
 * Storing database access information into a thread local value provides metrics
 * information per http request.
 * On the other hand, using single instance to store database access information allows you to retrieve total accumulated
 * numbers since application has started.
 *
 * <ul>
 * <li> datasource name
 * <li> number of database call
 * <li> total query execution time
 * <li> number of queries by type
 * </ul>
 *
 * @author complone
 */
public class DataSourceQueryCountListener implements QueryExecutionListener {

    // uses per thread implementation in default
    private QueryCountStrategy queryCountStrategy = new ThreadQueryCountHolder();

    @Override
    public void beforeQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
    }

    @Override
    public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        final String dataSourceName = execInfo.getDataSourceName();

        QueryCount count = this.queryCountStrategy.getOrCreateQueryCount(dataSourceName);

        // increment db call
        count.incrementTotal();
        if (execInfo.isSuccess()) {
            count.incrementSuccess();
        } else {
            count.incrementFailure();
        }

        // increment elapsed time
        final long elapsedTime = execInfo.getElapsedTime();
        count.incrementTime(elapsedTime);

        // increment statement type
        count.increment(execInfo.getStatementType());

        // increment query count
        for (QueryInfo queryInfo : queryInfoList) {
            final String query = queryInfo.getQuery();
            final QueryType type = QueryUtils.getQueryType(query);
            count.increment(type);
        }

    }

    /**
     * @since 1.4.2
     */
    public QueryCountStrategy getQueryCountStrategy() {
        return queryCountStrategy;
    }

    /**
     * @since 1.4.2
     */
    public void setQueryCountStrategy(QueryCountStrategy queryCountStrategy) {
        this.queryCountStrategy = queryCountStrategy;
    }

}
