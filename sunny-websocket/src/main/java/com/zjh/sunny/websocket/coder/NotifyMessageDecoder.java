package com.zjh.sunny.websocket.coder;

import com.alibaba.fastjson.JSONObject;
import com.zjh.sunny.core.constant.NetType;
import com.zjh.sunny.core.pojo.message.NotifyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * websocket 入站数据解码
 * @author zhangJinHui
 * @date 2019/12/6 17:54
 */
public class NotifyMessageDecoder extends ByteToMessageDecoder {

    private final Logger logger = LoggerFactory.getLogger(NotifyMessageDecoder.class);

    //TODO:数据解密

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try {
            logger.debug("======= 入站消息解码 =======");

            //消息体长度
            final int length = in.readableBytes();

            int headLength = NetType.TCP.getHead().length();
            if (length < headLength) {
                ctx.channel().close();
                return;
            }

            int contentLength = length - headLength;

            in.readerIndex(headLength);

            byte[] requestByte = new byte[contentLength];
            in.readBytes(requestByte, 0, contentLength);

            NotifyMessage message = JSONObject.parseObject(requestByte, NotifyMessage.class);

            out.add(message);
        } catch (Exception e) {
            logger.error("NotifyMessageDecoder Error :", e);
            ctx.close();
        }
    }
}
