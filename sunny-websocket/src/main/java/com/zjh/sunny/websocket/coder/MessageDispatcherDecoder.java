package com.zjh.sunny.websocket.coder;

import com.zjh.sunny.core.constant.NetType;
import com.zjh.sunny.core.constant.NetAttributeKey;
import com.zjh.sunny.core.util.StringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 默认netty通讯解码
 * 根据类型，动态设置解码器和处理器
 * @author zhangJinHui
 * @date 2020/3/19 21:54
 */
public class MessageDispatcherDecoder extends ByteToMessageDecoder {

    private final Logger logger = LoggerFactory.getLogger(MessageDispatcherDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        ChannelPipeline pipeline = ctx.pipeline();
        NetType netType = getNetType(ctx, in);

        if (netType != null) {
            switch (netType) {
                case WEBSOCKET:
                    pipeline.remove("notifyMessageDecoder");
                    pipeline.remove("notifyMessageEncoder");
                    pipeline.remove("notifyHandler");
                    break;
                case HTTP:
                    pipeline.remove("notifyMessageDecoder");
                    pipeline.remove("notifyMessageEncoder");
                    pipeline.remove("notifyHandler");

                    pipeline.remove("webSocketMessageDecoder");
                    pipeline.remove("webSocketMessageEncoder");
                    pipeline.remove("webSocketHandler");
                    break;
                case TCP:
                    pipeline.remove("webSocketMessageDecoder");
                    pipeline.remove("webSocketMessageEncoder");
                    pipeline.remove("webSocketHandler");
                    break;
                default:
                    break;
            }
        }

        ctx.pipeline().remove(this);

        in.resetReaderIndex();
        ctx.fireChannelRead(in);
    }

    private NetType getNetType(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
        NetType netType = null;
        if (buffer.isReadable()) {
            int size = buffer.readableBytes();
            size = Math.min(size, 18);
            byte[] temp = new byte[size];
            buffer.getBytes(0, temp);
            String str = new String(temp);

            if (StringUtil.isEmpty(str)) {
                return null;
            }

            logger.debug("=== 数据头：{}", str);

            if (str.toUpperCase().contains("HTTP")) {
                netType = NetType.HTTP;
            } else {
                if (str.startsWith(NetType.TCP.getHead())) {
                    netType = NetType.TCP;
                } else if (str.startsWith(NetType.WEBSOCKET.getHead())) {
                    netType = NetType.WEBSOCKET;
                }
            }
        }
        //保存链接类型
        ctx.channel().attr(NetAttributeKey.LINK_TYPE).set(netType);
        return netType;
    }

    private void removeHttpHandle(ChannelPipeline pipeline) {
        pipeline.remove("http-codec");
        pipeline.remove("aggregator");
        pipeline.remove("http-chunked");
    }
}
