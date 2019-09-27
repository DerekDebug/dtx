package com.example.consumerserver.dtx.txmanager_client;

import com.alibaba.fastjson.JSONObject;
import com.example.consumerserver.dtx.txmanager_client.netty.NettyClient;
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

    //本地接收TxManager的netty客户端
    private static NettyClient nettyClient;

    //本地事务map
    public static Map<String, Tx> localTxMap = new HashMap<>();

    //threadlocal的 TxMap
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
     * 1、创建xid
     * 2、约定txCount
     * 3、发送给TxManager
     */
    public static String createTxGroup() {
        String xid = UUID.randomUUID().toString();
        //约定txCount
        setTxCount(0);
        setXid(xid);
        JSONObject obj = new JSONObject();
        obj.put("xid", xid);
        obj.put("command", "create");
        obj.put("txCount",txCount);
        nettyClient.call(obj);
        return xid;
    }

    /**
     * 创建本地的tx信息，保存在treadlocal中，方便后续同线程调用
     */
    public static Tx createTx(String xid) {
        String txId = "consumer";
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

    public static Tx getThreadLocalTx() {
        return txThreadLocal.get();
    }

}
