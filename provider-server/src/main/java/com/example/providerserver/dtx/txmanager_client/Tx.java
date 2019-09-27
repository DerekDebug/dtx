package com.example.providerserver.dtx.txmanager_client;

import com.example.providerserver.dtx.task.Task;

public class Tx {
    private String xid;

    private String txId;
    private TxActionType localTxActionType;
    private TxActionType globalTxActionType;

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

    private Task task;

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Tx(String xid, String txId) {
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
