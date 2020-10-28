package com.zjh.sunny.core.coder;

import com.zjh.sunny.core.constant.HeadConstant;
import com.zjh.sunny.core.constant.LinkType;
import com.zjh.sunny.core.constant.NetConstant;
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

        LinkType linkType = getLinkType(ctx, in);

        switch (linkType) {
            case TCP:
                removeHttpHandle(pipeline);
                break;
            case WEBSOCKET:
                removeTcpHandle(pipeline);
                break;
            case HTTP:
                removeTcpHandle(pipeline);
                removeWebSocketHandle(pipeline);
                break;
            default:
                break;
        }

//        if (linkType != HTTP) {
//            pipeline.remove("http-codec");
//            pipeline.remove("aggregator");
//            pipeline.remove("http-chunked");
//        }

        ctx.pipeline().remove(this);

//        in.resetReaderIndex();
//
//        ctx.fireChannelRead(in);
    }

    private LinkType getLinkType(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
        LinkType linkType = LinkType.TCP;
        if (buffer.isReadable()) {
            int size = buffer.readableBytes();
            size = Math.min(size, 18);
            byte[] temp = new byte[size];
            buffer.getBytes(0, temp);
            String str = new String(temp);

            logger.debug("=== 数据头：{}", str);
            if (str.toUpperCase().contains("HTTP")) {
                linkType = LinkType.HTTP;
            } else if (str.startsWith(HeadConstant.NOTIFY)){
                linkType = LinkType.TCP;
            } else {
                linkType = LinkType.WEBSOCKET;
            }
        }
        //保存链接类型
        ctx.channel().attr(NetConstant.LINK_TYPE).set(linkType);
        return linkType;
    }

    private void removeHttpHandle(ChannelPipeline pipeline) {
        pipeline.remove("http-codec");
        pipeline.remove("aggregator");
        pipeline.remove("http-chunked");
    }

    private void removeTcpHandle(ChannelPipeline pipeline) {
        pipeline.remove("notifyMessageDecoder");
    }

    private void removeWebSocketHandle(ChannelPipeline pipeline) {

    }
}
