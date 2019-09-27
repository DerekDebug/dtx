package com.example.providerserver.dtx.txmanager_client.netty;

import com.alibaba.fastjson.JSONObject;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class NettyClient implements InitializingBean {

    public NettyHandler nettyHandler;

    private Channel channel;

    public void start(String host, int port) throws Exception {

        this.nettyHandler = new NettyHandler();

        final EventLoopGroup group = new NioEventLoopGroup();

        Bootstrap b = new Bootstrap();
        b.group(group).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() { // 绑定连接初始化器
                    @Override
                    public void initChannel(SocketChannel socketChannel) throws Exception {
                        System.out.println("正在连接中...");
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline
                                .addLast("encoder", new StringEncoder())
                                .addLast("decoder", new StringDecoder())
                                .addLast("handler", nettyHandler);
                    }
                });

        final ChannelFuture future = b.connect(host, port).sync();

        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture arg0) throws Exception {
                if (future.isSuccess()) {
                    System.out.println("connecting SUCCESS..........");
                } else {
                    System.out.println("connecting FAILED..........");
                    future.cause().printStackTrace();
                    group.shutdownGracefully();
                }
            }
        });

        this.channel = future.channel();
    }

    public Channel getChannel() {
        return channel;
    }

    public void call(JSONObject jsonObject) {
        try {
            nettyHandler.toServer(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        start("localhost", 8055);
    }
}
