package com.example.consumerserver.dtx.txmanager_client;

import com.example.consumerserver.dtx.task.Task;

public class Tx {

    //一个分布式事务的组id：BranchId/GroupId
    private String xid;
    private String txId;

    //两个状态：本地tx的状态的和最终action的状态
    private TxActionType localTxActionType;
    private TxActionType globalTxActionType;
    private Task task;


    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public TxActionType getLocalTxActionType() {
        return localTxActionType;
    }

    public void setLocalTxActionType(TxActionType localTxActionType) {
        this.localTxActionType = localTxActionType;
    }

    public TxActionType getGlobalTxActionType() {
        return globalTxActionType;
    }

    public void setGlobalTxActionType(TxActionType globalTxActionType) {
        this.globalTxActionType = globalTxActionType;
    }

    public Tx(String xid ,String txId) {
        this.xid = xid;
        this.txId = txId;
        this.task = new Task();
    }

    public String getXid() {
        return xid;
    }

    public void setXid(String xid) {
        this.xid = xid;
    }


    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }


}
