package com.zjh.sunny.websocket.handle;

import com.zjh.sunny.websocket.manager.WebSocketRequestManager;
import com.zjh.sunny.websocket.message.WebSocketMessage;
import com.zjh.sunny.websocket.manager.WebSocketSessionManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * netty socket处理程序
 * @author zhangJinHui
 */
@Component
@ChannelHandler.Sharable
public class WebSocketHandler extends SimpleChannelInboundHandler<Object> {

    private final Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);

    private WebSocketServerHandshaker handshaker;

    @Autowired
    private WebSocketSessionManager webSocketSessionManager;

    @Autowired
    private WebSocketRequestManager webSocketRequestService;

    /**
     * 产生新链接时候
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.debug("======= netty收到请求, 客户端IP: {}, 请求消息：\r\n{}", ctx.channel().remoteAddress(), msg);
        if (msg instanceof FullHttpRequest) {
            handHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketMessage) {
            webSocketRequestService.executor(ctx, (WebSocketMessage) msg);
        }
    }

    /**
     * 当连接打开时，这里表示有数据将要进站
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        webSocketSessionManager.addChannel(ctx.channel());
    }

    /**
     * 当连接要关闭时
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.close();
        webSocketSessionManager.removeChannel(ctx.channel());
    }

    /**
     * 发生异常时
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("======= 连接异常：{}", cause.getMessage());
        ctx.close();
        webSocketSessionManager.removeChannel(ctx.channel());
    }

    /**
     * 处理 http 请求
     * WebSocket 初始握手 (opening handshake ) 都始于一个 HTTP 请求
     */
    private void handHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        logger.debug("======= 收到FullHttpRequest消息 =======");
        //如果HTTP解码失败，返回http异常
        if (!req.decoderResult().isSuccess() || !("websocket".equals(req.headers().get("Upgrade")))) {
            sendHttpResponse(ctx, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory(
                req.headers().get(HttpHeaderNames.HOST),
                null,
                false);
        handshaker = factory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }
    }

    /**
     * 响应 Http 初始握手请求
     */
    private void sendHttpResponse(ChannelHandlerContext ctx, DefaultFullHttpResponse res) {
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void handleWebSocketRequest(ChannelHandlerContext ctx, WebSocketFrame frame) {
        try {
            logger.debug("======= 收到WebSocketFrame消息 =======");

            // 判断是否是关闭链路的指令
            if (frame instanceof CloseWebSocketFrame) {
                handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
                return;
            }

            // 判断是否是Ping消息
            if (frame instanceof PingWebSocketFrame) {
                ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
                return;
            }

            //收到文本消息
            if (frame instanceof TextWebSocketFrame) {
                logger.debug("======= data: {}", ((TextWebSocketFrame) frame).text());
//                if (nettyWebSocketProperties.isTextMessage()) {
//                    String requestData = ((TextWebSocketFrame) frame).text();
//                    NetMessage requestMsg = JSONObject.parseObject(requestData, NetMessage.class);
//                    webSocketRequestService.executor(ctx, requestMsg);
//                } else {
//                    NetMessage responseMsg = new NetMessage(ErrorCode.ERROR, "暂不支持该数据类型！");
//                    ctx.channel().writeAndFlush(responseMsg);
//                }
            }
        } catch (Exception e) {
            logger.error("handleWebSocketRequest Err: ", e);
        }
    }
}
