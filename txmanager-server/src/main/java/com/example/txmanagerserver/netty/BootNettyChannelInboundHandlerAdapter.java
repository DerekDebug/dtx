package com.example.txmanagerserver.netty;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 写实际业务逻辑
 */
public class BootNettyChannelInboundHandlerAdapter extends ChannelInboundHandlerAdapter {

    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private static Map<String, List<String>> txStatusMap = new HashMap<>();

    private static Map<String, Boolean> isEndMap = new HashMap<>();

    private static Map<String, Integer> txCountMap = new HashMap<>();

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.add(channel);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception, IOException {
        System.out.println("channelRead:read msg:" + msg.toString());
        try {
            JSONObject obj = JSON.parseObject((String) msg);
            String command = obj.getString("command");
            String xid = obj.getString("xid");
            String txActionType = obj.getString("txActionType");
            Integer txCount = obj.getInteger("txCount");
            Boolean isEnd = obj.getBoolean("isEnd");
            /**
             *命令模式：command这里只有2种：create/add；Hystrix等很多地方都有命令模式，有缘再见
             */
            if (command.equals("create")) {
                txStatusMap.put(xid, new ArrayList<>());
                txCountMap.put(xid, txCount);
            } else if (command.equals("add")) {
                txStatusMap.get(xid).add(txActionType);
                if (isEnd) {
                    isEndMap.put(xid, true);
                }
                JSONObject result = new JSONObject();
                result.put("xid", xid);

                //判断条件：
                //      这里的判断条件见仁见智了
                if (isEndMap.get(xid) &&(txStatusMap.get(xid).size()==txCountMap.get(xid))) {
                    if (txStatusMap.get(xid).contains("ROLLBACK")) {
                        System.out.println("return rollback-----------");
                        result.put("status", 1);
                        toClient(result);
                    } else {
                        System.out.println("return commit-----------");
                        result.put("status", 0);
                        toClient(result);
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    /**
     * 从客户端收到新的数据、读取完成时调用
     * @param ctx
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws IOException {
        System.out.println("channelReadComplete");
        ctx.flush();
    }

    /**
     * 当出现 Throwable 对象才会被调用，即当 Netty 由于 IO 错误或者处理器在处理事件时抛出的异常时
     *
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws IOException {
        System.out.println("exceptionCaught");
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 客户端与服务端第一次建立连接时 执行
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception, IOException {
        super.channelActive(ctx);
        ctx.channel().read();
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = insocket.getAddress().getHostAddress();
        //此处不能使用ctx.close()，否则客户端始终无法与服务端建立连接
        System.out.println("channelActive:" + clientIp + ctx.name());
    }

    /**
     * 客户端与服务端 断连时 执行
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception, IOException {
        super.channelInactive(ctx);
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = insocket.getAddress().getHostAddress();
        ctx.close(); //断开连接时，必须关闭，否则造成资源浪费，并发量很大情况下可能造成宕机
        System.out.println("channelInactive:" + clientIp);
    }

    /**
     * 服务端当read超时, 会调用这个方法
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception, IOException {
        super.userEventTriggered(ctx, evt);
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = insocket.getAddress().getHostAddress();
        ctx.close();//超时时断开连接
        System.out.println("userEventTriggered:" + clientIp);
    }

    /**
     * 这里必须是writeAndFlush()，不能写write()否则只有一个netty-client能收到消息
     */
    public void toClient(JSONObject obj) {
        for (Channel channel : channelGroup) {
            channel.writeAndFlush(obj.toString());
        }
    }
}



