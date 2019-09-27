//package com.example.consumerserver.dtx.connection;
//
//import com.zaxxer.hikari.pool.PoolEntry;
//import com.zaxxer.hikari.pool.ProxyConnection;
//import com.zaxxer.hikari.pool.ProxyLeakTask;
//import com.zaxxer.hikari.util.FastList;
//
//import java.sql.*;
//import java.util.Map;
//import java.util.Properties;
//import java.util.concurrent.Executor;
//
//public class TestConnection2 extends ProxyConnection {
//    protected TestConnection2(PoolEntry poolEntry, Connection connection, FastList<Statement> openStatements, ProxyLeakTask leakTask, long now, boolean isReadOnly, boolean isAutoCommit) {
//        super(poolEntry, connection, openStatements, leakTask, now, isReadOnly, isAutoCommit);
//    }
//
//    @Override
//    public String nativeSQL(String sql) throws SQLException {
//        return null;
//    }
//
//    @Override
//    public boolean getAutoCommit() throws SQLException {
//        return false;
//    }
//
//    @Override
//    public boolean isReadOnly() throws SQLException {
//        return false;
//    }
//
//    @Override
//    public String getCatalog() throws SQLException {
//        return null;
//    }
//
//    @Override
//    public int getTransactionIsolation() throws SQLException {
//        return 0;
//    }
//
//    @Override
//    public SQLWarning getWarnings() throws SQLException {
//        return null;
//    }
//
//    @Override
//    public void clearWarnings() throws SQLException {
//
//    }
//
//    @Override
//    public Map<String, Class<?>> getTypeMap() throws SQLException {
//        return null;
//    }
//
//    @Override
//    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
//
//    }
//
//    @Override
//    public void setHoldability(int holdability) throws SQLException {
//
//    }
//
//    @Override
//    public int getHoldability() throws SQLException {
//        return 0;
//    }
//
//    @Override
//    public Savepoint setSavepoint() throws SQLException {
//        return null;
//    }
//
//    @Override
//    public Savepoint setSavepoint(String name) throws SQLException {
//        return null;
//    }
//
//    @Override
//    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
//
//    }
//
//    @Override
//    public Clob createClob() throws SQLException {
//        return null;
//    }
//
//    @Override
//    public Blob createBlob() throws SQLException {
//        return null;
//    }
//
//    @Override
//    public NClob createNClob() throws SQLException {
//        return null;
//    }
//
//    @Override
//    public SQLXML createSQLXML() throws SQLException {
//        return null;
//    }
//
//    @Override
//    public boolean isValid(int timeout) throws SQLException {
//        return false;
//    }
//
//    @Override
//    public void setClientInfo(String name, String value) throws SQLClientInfoException {
//
//    }
//
//    @Override
//    public void setClientInfo(Properties properties) throws SQLClientInfoException {
//
//    }
//
//    @Override
//    public String getClientInfo(String name) throws SQLException {
//        return null;
//    }
//
//    @Override
//    public Properties getClientInfo() throws SQLException {
//        return null;
//    }
//
//    @Override
//    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
//        return null;
//    }
//
//    @Override
//    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
//        return null;
//    }
//
//    @Override
//    public String getSchema() throws SQLException {
//        return null;
//    }
//
//    @Override
//    public void abort(Executor executor) throws SQLException {
//
//    }
//
//    @Override
//    public int getNetworkTimeout() throws SQLException {
//        return 0;
//    }
//}
