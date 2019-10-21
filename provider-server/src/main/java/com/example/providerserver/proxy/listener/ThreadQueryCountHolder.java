package com.example.providerserver.proxy.listener;

import com.example.providerserver.proxy.QueryCount;
import com.example.providerserver.proxy.QueryCountHolder;

/**
 * Uses {@link QueryCountHolder} which uses thread local to hold {@link QueryCount}.
 *
 * @author complone
 * @since 1.4.2
 */
public class ThreadQueryCountHolder implements QueryCountStrategy {

    @Override
    public QueryCount getOrCreateQueryCount(String dataSourceName) {
        QueryCount queryCount = QueryCountHolder.get(dataSourceName);
        if (queryCount == null) {
            queryCount = new QueryCount();
            QueryCountHolder.put(dataSourceName, queryCount);
        }
        return queryCount;
    }

}
