package com.zjh.sunny.core.sender;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhangJinHui
 * @date 2020/3/19 21:58
 */
@ChannelHandler.Sharable
public class NodeHeartBeatClientHandler extends IdleStateHandler {

    private final Logger logger = LoggerFactory.getLogger(NodeHeartBeatClientHandler.class);

    private static final int READ_IDLE_GAP = 150;

    public NodeHeartBeatClientHandler(int readerIdleTimeSeconds, int writerIdleTimeSeconds, int allIdleTimeSeconds) {
        super(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds);
    }


    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
//        //判断消息实例
//        if (null == msg || !(msg instanceof ProtoMsg.Message)) {
//            super.channelRead(ctx, msg);
//            return;
//        }
//
//        ProtoMsg.Message pkg = (ProtoMsg.Message) msg;
//        //判断消息类型
//        ProtoMsg.HeadType headType = pkg.getType();
//        if (headType.equals(ProtoMsg.HeadType.HEART_BEAT)) {
//            //异步处理,将心跳包，直接回复给客户端
//            FutureTaskScheduler.add(() -> {
//                if (ctx.channel().isActive()) {
//                    ctx.writeAndFlush(msg);
//                }
//            });
//
//        }
        super.channelRead(ctx, msg);

    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
//        logger.info(READ_IDLE_GAP + "秒内未读到数据，关闭连接",ctx.channel().attr(ServerConstants.CHANNEL_NAME).get());
//        SessionManger.inst().closeSession(ctx);
    }
}
