package com.example.consumerserver.dtx.txmanager_client.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.consumerserver.dtx.txmanager_client.Tx;
import com.example.consumerserver.dtx.txmanager_client.TxActionType;
import com.example.consumerserver.dtx.txmanager_client.TxProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 供NettyClient写业务的Handler
 */
public class NettyHandler extends ChannelInboundHandlerAdapter {

    private ChannelHandlerContext channelHandlerContext;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        channelHandlerContext = ctx;
    }

    @Override
    public  void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("Netty client :  --------- "+msg.toString());
        JSONObject obj = JSON.parseObject((String) msg);
        String xid = obj.getString("xid");
        Integer status = obj.getInteger("status");
        Tx tx = TxProcessor.getTx(xid);
        //通过TxManager的返回status值，设入最终commit/rollback
        //但是这里注意，如果本地tx是rollback状态
        //我直接不处理了（偷懒嘛，跟之前在InterceptedConnection的注解写的，我先滚了）
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
