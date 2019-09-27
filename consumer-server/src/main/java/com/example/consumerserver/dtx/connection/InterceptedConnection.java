package com.example.consumerserver.dtx.connection;

import com.example.consumerserver.dtx.txmanager_client.Tx;
import com.example.consumerserver.dtx.txmanager_client.TxActionType;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * 自定义一个拦截connection
 * 特别提醒，我们不仅仅要重写：
 *  commit()
 *  rollback()
 *  close()
 *  这3个方法，有一个大坑：setAutoCommit()
 *  这里传false，不然给你自动commit了，鄙人对javax.sql等底层包无知，具体的各种方法，尚待学习
 */
public class InterceptedConnection implements Connection {

    private Connection connection;
    private Tx tx;

    public InterceptedConnection(Connection connection, Tx tx) {
        this.connection = connection;
        this.tx = tx;
    }


    /***
     * WARNNING:重点
     *
     * 在这里，我们新开了一个线程，使其阻塞，目的就是不进行commit/rollback，等待TxManager最终通知
     * 阿里的SEATA框架在这里使用的是：
     *    @Override
     *     public void commit() throws SQLException {
     *         try {
     *             LOCK_RETRY_POLICY.execute(() -> {
     *                 doCommit();
     *                 return null;
     *             });
     *         } catch (SQLException e) {
     *             throw e;
     *         } catch (Exception e) {
     *             throw new SQLException(e);
     *         }
     *     }
     *
     *         public <T> T execute(Callable<T> callable) throws Exception {
     *             if (LOCK_RETRY_POLICY_BRANCH_ROLLBACK_ON_CONFLICT) {
     *                 return callable.call();
     *             } else {
     *                 return doRetryOnLockConflict(callable);
     *             }
     *         }

     *                 protected <T> T doRetryOnLockConflict(Callable<T> callable) throws Exception {
     *             LockRetryController lockRetryController = new LockRetryController();
     *             while (true) {
     *                 try {
     *                     return callable.call();
     *                 } catch (LockConflictException lockConflict) {
     *                     onException(lockConflict);
     *                     lockRetryController.sleep(lockConflict);
     *                 } catch (Exception e) {
     *                     onException(e);
     *                     throw e;
     *                 }
     *             }
     *         }
     *
     * 其也在此处也上锁，使其阻塞，还设置了timeout
     *             public void sleep(Exception e) throws LockWaitTimeoutException {
     *         if (--lockRetryTimes < 0) {
     *             throw new LockWaitTimeoutException("Global lock wait timeout", e);
     *         }
     *
     *         try {
     *             Thread.sleep(lockRetryInternal);
     *         } catch (InterruptedException ignore) {
     *         }
     *     }
     *
     * 还是开个线程最简单了
     */
    @Override
    public void commit() {
        try {
            new Thread(() -> {
                tx.getTask().waitting();
                //通过GlobalTxActionType的最终状态，来判断commit/rollback
                if (tx.getGlobalTxActionType().equals(TxActionType.COMMIT)) {
                    try {
                        connection.commit();
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {

        }

    }

    /**
     * 这里有2种逻辑：
     * 1、一旦本地tx状态为rollback，不等你txManager给我返回信息了，我先滚为敬
     * 2、当然，你也可以让所有的本地tx都在其等待，我偷懒选择 1
     */
    @Override
    public void rollback() throws SQLException {
        connection.rollback();

    }

    @Override
    public void close() {
        //这里不要加，上面已经close了，你还加个锤子
    }

    @Override
    public Statement createStatement() throws SQLException {
        return connection.createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return connection.prepareCall(sql);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return connection.nativeSQL(sql);
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        if (connection != null) {
            connection.setAutoCommit(false);
        }
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return connection.getAutoCommit();
    }


    @Override
    public boolean isClosed() throws SQLException {
        return connection.isClosed();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return connection.getMetaData();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        connection.setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return connection.isReadOnly();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        connection.setCatalog(catalog);
    }

    @Override
    public String getCatalog() throws SQLException {
        return connection.getCatalog();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        connection.setTransactionIsolation(level);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return connection.getTransactionIsolation();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return connection.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        connection.clearWarnings();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return connection.createStatement(resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return connection.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return connection.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return connection.getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        connection.setTypeMap(map);
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        connection.setHoldability(holdability);
    }

    @Override
    public int getHoldability() throws SQLException {
        return connection.getHoldability();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return connection.setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return connection.setSavepoint(name);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        connection.rollback(savepoint);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        connection.releaseSavepoint(savepoint);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return connection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return connection.prepareStatement(sql, autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return connection.prepareStatement(sql, columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return connection.prepareStatement(sql, columnNames);
    }

    @Override
    public Clob createClob() throws SQLException {
        return connection.createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        return connection.createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return connection.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return connection.createSQLXML();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return connection.isValid(timeout);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        connection.setClientInfo(name, value);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        connection.setClientInfo(properties);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return connection.getClientInfo(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return connection.getClientInfo();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return connection.createArrayOf(typeName, elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return connection.createStruct(typeName, attributes);
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        connection.setSchema(schema);
    }

    @Override
    public String getSchema() throws SQLException {
        return connection.getSchema();
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        connection.abort(executor);
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        connection.setNetworkTimeout(executor, milliseconds);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return connection.getNetworkTimeout();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return connection.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return connection.isWrapperFor(iface);
    }
}
