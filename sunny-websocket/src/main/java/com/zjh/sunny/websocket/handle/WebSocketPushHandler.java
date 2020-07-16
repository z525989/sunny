package com.zjh.sunny.websocket.handle;

import com.zjh.sunny.websocket.session.SessionManager;
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
    private SessionManager sessionManager;

//    @Autowired
//    private WebSocketSessionDao webSocketSessionDao;
//
//    @Autowired
//    private ServerNodeManager serverNodeManager;

    /**
     * 给指定用户回复消息
     */
    public void sendStrToUser(ChannelHandlerContext ctx, String msg) {
        Channel channel = ctx.channel();
        channel.writeAndFlush(new TextWebSocketFrame(msg));
    }

    /**
     * 给全部用户回复消息
     */
    public void sendStrToAll(String msg) {
        sessionManager.getChannelGroup().forEach(channel -> channel.writeAndFlush(new TextWebSocketFrame(msg)));
    }

    /**
     * 广播消息（不给自己发）
     */
    public void broadcastStrToUser(ChannelHandlerContext ctx, String msg) {
        sessionManager.getChannelGroup().stream()
                .filter(channel -> channel.id() != ctx.channel().id())
                .forEach(channel -> channel.writeAndFlush(new TextWebSocketFrame(msg)));
    }


    public void sendWsMsgToUser(ChannelHandlerContext ctx, Object msg) {
        sessionManager.getChannelGroup().stream()
                .filter(channel -> channel.id() == ctx.channel().id())
                .forEach(channel -> channel.writeAndFlush(msg));
    }

//    public void sendWsMsgToUser(long userId, Object msg) {
//        Channel channel = sessionManager.getChannelByUserId(userId);
//        if (channel != null) {
//            channel.writeAndFlush(msg);
//            return;
//        }
//        WebSocketSession session = webSocketSessionDao.getWebSocketSessionByUserId(userId);
//        if (session != null) {
//            serverNodeManager.forwardMsgToRemoteNode(session, msg);
//        }
//    }

    public void sendWsMsgToUser(String sessionId, Object msg) {
        Channel channel = sessionManager.getChannelBySessionId(sessionId);
        if (channel == null) {
            return;
        }
        channel.writeAndFlush(msg);
    }

    public void sendWsMsgToUser(Object msg, Channel channel) {
        if (channel == null) {
            logger.error("channel is null");
            return;
        }
        channel.writeAndFlush(msg);
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
        sessionManager.getChannelGroup().stream()
                .filter(channel -> channel.id() != ctx.channel().id())
                .forEach(channel -> channel.writeAndFlush(msg));
    }

}
