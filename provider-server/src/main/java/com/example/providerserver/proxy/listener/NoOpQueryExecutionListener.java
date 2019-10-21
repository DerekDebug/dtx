package com.example.providerserver.proxy.listener;

import com.example.providerserver.proxy.ExecutionInfo;
import com.example.providerserver.proxy.QueryInfo;

import java.util.List;



/**
 * No operation implementation of {@link QueryExecutionListener}
 *
 * @author complone
 * @since 1.2
 */
public class NoOpQueryExecutionListener implements QueryExecutionListener {

    @Override
    public void beforeQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        // do nothing
    }

    @Override
    public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        // do nothing
    }
}
