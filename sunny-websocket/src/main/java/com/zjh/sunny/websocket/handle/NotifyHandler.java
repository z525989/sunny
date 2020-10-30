package com.zjh.sunny.websocket.handle;

import com.zjh.sunny.core.pojo.message.NotifyMessage;
import com.zjh.sunny.websocket.manager.WebSocketServerNodeManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhangJinHui
 * @date 2020/10/30 11:39
 */
@Component
@ChannelHandler.Sharable
public class NotifyHandler extends SimpleChannelInboundHandler<Object> {

    private final Logger logger = LoggerFactory.getLogger(NotifyHandler.class);

    @Autowired
    private WebSocketServerNodeManager webSocketServerNodeManager;

    /**
     * 产生新链接时候
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.debug("======= NotifyHandler收到请求, 客户端IP: {}, 请求消息：\r\n{}", ctx.channel().remoteAddress(), msg);
        if (msg instanceof NotifyMessage) {
            webSocketServerNodeManager.receiveNotify(ctx, (NotifyMessage) msg);
        }
    }

    /**
     * 当连接打开时，这里表示有数据将要进站
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("====channelActive");

        //发送链接成功的通知到远程节点
        NotifyMessage notifyMessage = new NotifyMessage();
        notifyMessage.setType(NotifyMessage.SESSION_ON);

        ctx.writeAndFlush(notifyMessage);
    }

    /**
     * 当连接要关闭时
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //发送链接关闭的通知到远程节点
        NotifyMessage notifyMessage = new NotifyMessage();
        notifyMessage.setType(NotifyMessage.SESSION_OFF);

        ctx.writeAndFlush(notifyMessage);

        ctx.close();
    }

    /**
     * 发生异常时
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("======= 连接异常：{}", cause.getMessage());
        ctx.close();
    }
}
