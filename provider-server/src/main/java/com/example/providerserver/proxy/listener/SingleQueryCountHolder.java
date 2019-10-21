package com.example.providerserver.proxy.listener;

import com.example.providerserver.proxy.QueryCount;
import com.example.providerserver.proxy.QueryCountHolder;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;



/**
 * Use single instance to hold
 *
 * The {@link QueryCount} holds total accumulated values from all threads where database access has performed.
 *
 * When {@link #populateQueryCountHolder} is set to {@code true}(default), it populates {@link QueryCountHolder}.
 *
 * @author complone
 * @since 1.4.2
 */
public class SingleQueryCountHolder implements QueryCountStrategy {

    private ConcurrentMap<String, QueryCount> queryCountMap = new ConcurrentHashMap<String, QueryCount>();
    private boolean populateQueryCountHolder = true;

    @Override
    public QueryCount getOrCreateQueryCount(String dataSourceName) {
        QueryCount queryCount = queryCountMap.get(dataSourceName);
        if (queryCount == null) {
            queryCountMap.putIfAbsent(dataSourceName, new QueryCount());
            queryCount = queryCountMap.get(dataSourceName);
        }
        if (this.populateQueryCountHolder) {
            QueryCountHolder.put(dataSourceName, queryCount);
        }
        return queryCount;
    }

    public ConcurrentMap<String, QueryCount> getQueryCountMap() {
        return queryCountMap;
    }

    public void setQueryCountMap(ConcurrentMap<String, QueryCount> queryCountMap) {
        this.queryCountMap = queryCountMap;
    }

    public boolean isPopulateQueryCountHolder() {
        return populateQueryCountHolder;
    }

    public void setPopulateQueryCountHolder(boolean populateQueryCountHolder) {
        this.populateQueryCountHolder = populateQueryCountHolder;
    }

    public void clear() {
        this.queryCountMap.clear();
    }

}
