package com.zjh.sunny.core.coder;

import com.alibaba.fastjson.JSONObject;
import com.zjh.sakura.common.websocket.pojo.ProtoObject;
import com.zjh.sakura.common.websocket.pojo.message.NetMessage;
import com.zjh.sakura.common.utils.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * websocket 入站数据解码
 * @author zhangJinHui
 * @date 2019/12/6 17:54
 */
public class WebSocketMessageDecoder extends MessageToMessageDecoder<BinaryWebSocketFrame> {

    private final Logger logger = LoggerFactory.getLogger(WebSocketMessageDecoder.class);

    //TODO:数据解密

    @Override
    protected void decode(ChannelHandlerContext ctx, BinaryWebSocketFrame frame, List<Object> out) throws Exception {
        try {
            logger.debug("======= 入站消息解码 =======");

            ByteBuf buf = frame.content();
            final int length = buf.readableBytes();

            byte[] requestByte = new byte[length];
            buf.readBytes(requestByte);

            String sessionId = ctx.channel().id().asLongText();

            if (ByteUtil.isJson(requestByte)) {
                NetMessage msg = JSONObject.parseObject(requestByte, NetMessage.class);

                msg.setSessionId(sessionId);

                out.add(msg);
            } else {
                //protoBuf
                ProtoObject.ProtoMessage protoMessage = ProtoObject.ProtoMessage.parseFrom(requestByte);
                if (protoMessage != null) {
                    protoMessage.toBuilder().setSessionId(sessionId);
                    out.add(protoMessage);
                }
            }
        } catch (Exception e) {
            logger.error("WebSocketMessageDecoder Error :", e);
            ctx.close();
        }
    }
}
