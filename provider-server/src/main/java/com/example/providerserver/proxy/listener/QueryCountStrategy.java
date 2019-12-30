package com.example.providerserver.proxy.listener;

import com.example.providerserver.proxy.QueryCount;

/**
 * @author complone
 * @see DataSourceQueryCountListener
 * @since 1.4.2
 */
public interface QueryCountStrategy {

    QueryCount getOrCreateQueryCount(String dataSourceName);

}
