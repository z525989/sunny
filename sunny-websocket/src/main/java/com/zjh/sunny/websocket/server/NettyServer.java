package com.zjh.sunny.websocket.server;

import com.zjh.sunny.websocket.WebSocketProperties;
import com.zjh.sunny.websocket.handle.NettyInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * netty server
 */
@Component
@ChannelHandler.Sharable
public class NettyServer implements Runnable{

    private final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private final EventLoopGroup bossGroup = new NioEventLoopGroup();

    private final EventLoopGroup workGroup = new NioEventLoopGroup();

    @Autowired
    private WebSocketProperties webSocketProperties;

    @Autowired
    private NettyInitializer nettyInitializer;

    @Override
    public void run() {
        try {
            int port = webSocketProperties.getPort();

            logger.info("---------------- Netty WebSocket Server Port: {} ----------------", port);
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(bossGroup, workGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(nettyInitializer);

            //最大客户端连接数
            bootstrap.option(ChannelOption.SO_BACKLOG, 10000);

            //重用地址
            bootstrap.option(ChannelOption.SO_REUSEADDR, true);

            bootstrap.option(ChannelOption.SO_RCVBUF, 1024);
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);

            //是否启用心跳保活机制
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

            //禁用Nagle算法，降低延迟
            bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
            bootstrap.childOption(ChannelOption.SO_SNDBUF, 1024);

            bootstrap.childOption(ChannelOption.SO_RCVBUF, 1024);
            bootstrap.childOption(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(4096));

            //内存池（减少GC）
            //Boss线程内存池配置
            bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            //Work线程内存池配置
            bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

            ChannelFuture future = bootstrap.bind(port).sync();

            Channel channel = future.channel();
            channel.closeFuture().sync();
        } catch (Exception e) {
            logger.error("netty server start error", e);
        } finally {
            logger.info("netty server stop");
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

}
