package com.zjh.sunny.websocket.coder;

import com.alibaba.fastjson.JSONObject;
import com.zjh.sunny.websocket.message.WebSocketMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * websocket 出站数据格式化
 * @author zhangJinHui
 * @date 2019/12/6 17:53
 */
public class WebSocketMessageEncoder extends MessageToMessageEncoder<WebSocketMessage> {

    private final Logger logger = LoggerFactory.getLogger(WebSocketMessageEncoder.class);

    //TODO:数据加密

    @Override
    protected void encode(ChannelHandlerContext ctx, WebSocketMessage msg, List<Object> out) throws Exception {
        logger.debug("======= 出站数据编码 =======");
        try {
            //将对象转换为byte
            byte[] context = JSONObject.toJSONBytes(msg);

            BinaryWebSocketFrame webSocketFrame = new BinaryWebSocketFrame();
            webSocketFrame.content().writeBytes(context);
            out.add(webSocketFrame);
        } catch (Exception e) {
            logger.error("WebSocketMessageEncoder Error: ", e);
            ctx.close();
        }
    }
}
