package com.example.providerserver.dtx.txmanager_client;

import com.alibaba.fastjson.JSONObject;
import com.example.providerserver.dtx.txmanager_client.netty.NettyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 我们需要一个事务处理器，去专门处理怎么去管理原本只交给spring处理的事务
 */
@Component
public class TxProcessor {

    private static NettyClient nettyClient;

    public static Map<String, Tx> localTxMap = new HashMap<>();

    public static ThreadLocal<Tx> txThreadLocal = new ThreadLocal<>();

    public static String xid;

    public static String getXid() {
        return xid;
    }

    public static void setXid(String xid) {
        TxProcessor.xid = xid;
    }

    public static Integer getTxCount() {
        return txCount;
    }

    public static void setTxCount(Integer txCount) {
        TxProcessor.txCount = txCount;
    }

    public static Integer txCount;

    @Autowired
    public void setBootNettyChannelInboundHandlerAdapter(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }

    /**
     * 并非一个分布式事务的发起者，自然不用创建xid
     */
    public static String createTxGroup() {
        String xid = UUID.randomUUID().toString();
        JSONObject obj = new JSONObject();
        obj.put("xid", xid);
        obj.put("command", "create");
        nettyClient.call(obj);
        return xid;
    }

    public static Tx createTx(String xid) {
        String txId = "provider";
        Tx tx = new Tx(xid, txId);
        txThreadLocal.set(tx);
        localTxMap.put(xid, tx);
        return tx;

    }

    public static void addTx(Tx tx, Boolean isEnd, TxActionType txActionType) {
        JSONObject obj = new JSONObject();
        obj.put("xid", tx.getXid());
        obj.put("txId", tx.getTxId());
        obj.put("txActionType", txActionType);
        obj.put("command", "add");
        obj.put("isEnd", isEnd);
        nettyClient.call(obj);
    }

    public static Tx getTx(String xid) {
        return localTxMap.get(xid);
    }

    public static Tx getThreadnLocalTx() {
        return txThreadLocal.get();
    }
}
