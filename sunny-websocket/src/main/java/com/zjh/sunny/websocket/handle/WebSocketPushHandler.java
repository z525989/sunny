package com.zjh.sunny.websocket.handle;

import com.zjh.sunny.websocket.node.WebSocketServerNodeManager;
import com.zjh.sunny.websocket.session.WebSocketSession;
import com.zjh.sunny.websocket.session.WebSocketSessionManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 推送管理器
 * @author zhangJinHui
 */
@Service
public class WebSocketPushHandler {

    private final Logger logger = LoggerFactory.getLogger(WebSocketPushHandler.class);

    @Autowired
    private WebSocketSessionManager webSocketSessionManager;

    @Autowired
    private WebSocketServerNodeManager webSocketServerNodeManager;

    /**
     * 给指定用户回复消息
     */
    public void sendStrToUser(ChannelHandlerContext ctx, String msg) {
        Channel channel = ctx.channel();
        channel.writeAndFlush(new TextWebSocketFrame(msg));
    }

    /**
     * 给当前服务器全部用户回复消息
     */
    public void sendStrToLocalServerAllUser(String msg) {
        webSocketSessionManager.getChannelGroup().forEach(channel -> channel.writeAndFlush(new TextWebSocketFrame(msg)));
    }

    /**
     * 广播消息（不给自己发）
     */
    public void broadcastStrToUser(ChannelHandlerContext ctx, String msg) {
        webSocketSessionManager.getChannelGroup().stream()
                .filter(channel -> channel.id() != ctx.channel().id())
                .forEach(channel -> channel.writeAndFlush(new TextWebSocketFrame(msg)));
    }

    public void sendWsMsgToLocalUserBySessionId(String sessionId, Object msg) {
        Channel channel = webSocketSessionManager.getChannelBySessionId(sessionId);
        if (channel == null) {
            return;
        }
        channel.writeAndFlush(msg);
    }

    public void sendWsMsgToLocalUser(Channel channel, Object msg) {
        if (channel == null) {
            logger.error("sendWsMsgToLocalUser error channel is null");
            return;
        }
        channel.writeAndFlush(msg);
    }

    /**
     * 根据userId发送消息
     * 如果userId不是当前节点用户，会转发到userId所在节点
     */
    public void sendWsMsgToUser(long userId, Object msg) {
        Channel channel = webSocketSessionManager.getChannelByUserId(userId);
        if (channel != null) {
            channel.writeAndFlush(msg);
            return;
        }

        //判断是否为远程节点
        WebSocketSession webSocketSession = webSocketSessionManager.getWebSocketSessionByUserId(userId);
        if (webSocketSession == null) {
            logger.error("sendWsMsgToUser getRemoteUserSession error");
            return;
        }

        webSocketServerNodeManager.forwardMsgToRemoteNode(webSocketSession, msg);
    }

    public void sendByteMsgToUser(ByteBuf msg, Channel channel) {
        if (channel == null) {
            return;
        }
        channel.writeAndFlush(new BinaryWebSocketFrame(msg));
    }

    public void sendTextMsgToUser(String msg, Channel channel) {
        if (channel == null) {
            logger.error("channel is null");
            return;
        }
        channel.writeAndFlush(new TextWebSocketFrame(msg));
    }


    /**
     * 广播数据（不包含自己）
     */
    public void broadcastWsMsg(ChannelHandlerContext ctx, Object msg) {
        webSocketSessionManager.getChannelGroup().stream()
                .filter(channel -> channel.id() != ctx.channel().id())
                .forEach(channel -> channel.writeAndFlush(msg));
    }

}
