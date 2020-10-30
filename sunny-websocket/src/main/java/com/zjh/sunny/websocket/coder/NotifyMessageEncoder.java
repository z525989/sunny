package com.zjh.sunny.websocket.coder;

import com.alibaba.fastjson.JSONObject;
import com.zjh.sunny.core.constant.NetType;
import com.zjh.sunny.core.pojo.message.NotifyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * tcp 出站数据格式化
 * @author zhangJinHui
 * @date 2019/12/6 17:53
 */
public class NotifyMessageEncoder extends MessageToByteEncoder<NotifyMessage> {

    private final Logger logger = LoggerFactory.getLogger(NotifyMessageEncoder.class);

    //TODO:数据加密

    @Override
    protected void encode(ChannelHandlerContext ctx, NotifyMessage msg, ByteBuf out) throws Exception {
        logger.debug("======= 出站数据编码 =======");
        try {
            //自定义head消息
            byte[] head = NetType.TCP.getHead().getBytes();

            //将对象转换为byte
            byte[] context = JSONObject.toJSONBytes(msg);

            out.writeBytes(head);
            out.writeBytes(context);
        } catch (Exception e) {
            logger.error("WebSocketMessageEncoder Error: ", e);
            ctx.close();
        }
    }
}
