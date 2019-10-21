package com.example.providerserver.proxy.listener;

import com.example.providerserver.proxy.ExecutionInfo;
import com.example.providerserver.proxy.QueryInfo;

import java.util.List;


/**
 * Listener interface. Inject the implementation to proxy handler interceptors.
 *
 * @author complone
 * @see ChainListener
 */
public interface QueryExecutionListener {

    static QueryExecutionListener DEFAULT = new NoOpQueryExecutionListener();

    void beforeQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList);

    void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList);
}
