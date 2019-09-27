package com.example.providerserver.dtx.txmanager_client.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.providerserver.dtx.txmanager_client.Tx;
import com.example.providerserver.dtx.txmanager_client.TxActionType;
import com.example.providerserver.dtx.txmanager_client.TxProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyHandler extends ChannelInboundHandlerAdapter {

    private ChannelHandlerContext channelHandlerContext;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channelHandlerContext = ctx;
    }

    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Netty client :  --------- "+msg.toString());
        JSONObject obj = JSON.parseObject((String) msg);
        String xid = obj.getString("xid");
        Integer status = obj.getInteger("status");
        Tx tx = TxProcessor.getTx(xid);
        if (status == 0){
            tx.setGlobalTxActionType(TxActionType.COMMIT);
            tx.getTask().release();
        }else if(status == 1 && tx.getLocalTxActionType().equals(TxActionType.COMMIT)){
            tx.setGlobalTxActionType(TxActionType.ROLLBACK);
            tx.getTask().release();
        }
    }

    public void toServer(JSONObject jsonObject) {
        channelHandlerContext.writeAndFlush(jsonObject.toString());
    }
}
