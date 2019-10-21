package com.example.providerserver.proxy;

import java.sql.Connection;
import java.util.Set;

/**
 * @author complone
 * @since 1.4.2
 */
public interface ConnectionIdManager {

    String getId(Connection connection);

    /**
     * Notify this manager that id of corresponding connection has addClosedId.
     *
     * @param closedId addClosedId connection id
     * @since 1.4.5
     */
    void addClosedId(String closedId);

    /**
     * Return set of connection ids that have not yet addClosedId.
     *
     * @return set of open connection ids
     * @since 1.4.5
     */
    Set<String> getOpenConnectionIds();

}