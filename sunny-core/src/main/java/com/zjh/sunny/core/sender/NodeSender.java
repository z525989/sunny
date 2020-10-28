package com.zjh.sunny.core.sender;

import com.zjh.sunny.core.coder.NotifyMessageEncoder;
import com.zjh.sunny.core.pojo.node.NettyServerNode;
import com.zjh.sunny.core.pojo.message.NotifyMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 节点通知器
 * @author zhangJinHui
 * @date 2020-3-19 18:37
 */
public class NodeSender {

    private final Logger logger = LoggerFactory.getLogger(NodeSender.class);

    /**
     * 节点信息
     */
    private NettyServerNode node;

    /**
     * netty client
     */
    private final Bootstrap bootstrap;

    /**
     * nio线程
     */
    private final EventLoopGroup workGroup;

    /**
     * 连接通道
     */
    private Channel channel;

    /**
     * 连接状态
     */
    private boolean connectFlag = false;

    /**
     * 构造函数
     */
    public NodeSender(NettyServerNode nettyServerNode) {
        this.node = nettyServerNode;

        //netty构建连接客户端
        this.bootstrap = new Bootstrap();

        //通过nio方式来接收连接和处理连接
        this.workGroup = new NioEventLoopGroup();
    }

    /**
     * netty连接关闭监听器
     */
    private GenericFutureListener<ChannelFuture> closeListener = (ChannelFuture f) -> {
        logger.info("======= 分布式连接已经断开, 节点信息：{}", node.toString());
        channel = null;
        connectFlag = false;
    };

    /**
     * netty连接监听器
     */
    private GenericFutureListener<ChannelFuture> connectedListener = (ChannelFuture f) -> {
        final EventLoop eventLoop = f.channel().eventLoop();
        if (!f.isSuccess()) {
            logger.info("======= 分布式节点连接失败!在10s之后准备尝试重连!");
            eventLoop.schedule(this::doConnect, 10, TimeUnit.SECONDS);
            connectFlag = false;
        } else {
            connectFlag = true;

            logger.info("======= 分布式节点连接成功:{}", node.toString());

            channel = f.channel();
            channel.closeFuture().addListener(closeListener);

            //发送链接成功的通知到远程节点
            NotifyMessage message = new NotifyMessage();
            message.setType(NotifyMessage.SESSION_ON);

            writeAndFlush(message);
        }
    };

    /**
     * 连接到远程节点
     */
    public void doConnect() {
        try {
            String host = node.getHost();
            int port =node.getPort();

            if (bootstrap.group() == null) {
                bootstrap.group(workGroup);
                bootstrap.channel(NioSocketChannel.class);
                bootstrap.option(ChannelOption.TCP_NODELAY, true);
                bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
                bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
                bootstrap.remoteAddress(host, port);

                // 设置通道初始化
                bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                    public void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();

                        pipeline.addLast(new NotifyMessageEncoder());

                        //自定义异常处理
                        pipeline.addLast("exceptionHandler",new NodeExceptionHandler());

                        //心跳处理
//                        pipeline.addLast(new NodeHeartBeatClientHandler());
                    }
                });

                logger.info("======= 开始连接分布式节点:{}", node.toString());

                ChannelFuture f = bootstrap.connect().sync();
                channel = f.channel();

                f.addListener(connectedListener);

//                f.channel().closeFuture().sync();
            } else {
                logger.info("======= 分布式节点重新连接, 节点信息: {}", node.toString());
                ChannelFuture f = bootstrap.connect();
                f.addListener(connectedListener);
            }
        } catch (Exception e) {
            logger.error("doConnect error: ", e);
        }
    }

    /**
     * 关闭远程节点连接
     */
    public void disConnect() {
        workGroup.shutdownGracefully();
        connectFlag = false;
    }

    /**
     * 发送数据到远程节点
     * @param pkg 数据内容
     */
    public void writeAndFlush(Object pkg) {
        if (!connectFlag) {
            logger.error("======= 分布式节点未连接: {}", node.toString());
            return;
        }
        channel.writeAndFlush(pkg);
    }

    public NettyServerNode getNode() {
        return node;
    }
}
