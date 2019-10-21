package com.example.providerserver.proxy.listener.logging;

import com.example.providerserver.proxy.ExecutionInfo;
import com.example.providerserver.proxy.QueryInfo;

import java.util.List;



/**
 * Generate logging entry.
 *
 * @author complone
 * @since 1.3
 */
public interface QueryLogEntryCreator {

    String getLogEntry(ExecutionInfo execInfo, List<QueryInfo> queryInfoList, boolean writeDataSourceName, boolean writeConnectionId);

}
