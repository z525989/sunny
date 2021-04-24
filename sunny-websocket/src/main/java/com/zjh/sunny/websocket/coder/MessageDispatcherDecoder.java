package com.zjh.sunny.websocket.coder;

import com.zjh.sunny.core.constant.LinkType;
import com.zjh.sunny.core.constant.NetAttributeKey;
import com.zjh.sunny.core.util.StringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.util.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 消息解码分发器
 * 1.解析连接类型
 * 2.根据连接类型，动态分配解码器和处理器
 *
 * @author zhangJinHui
 * @date 2020/3/19 21:54
 */
public class MessageDispatcherDecoder extends ByteToMessageDecoder {

    private final Logger logger = LoggerFactory.getLogger(MessageDispatcherDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        in.markReaderIndex();
        if (!in.isReadable()) {
            in.resetReaderIndex();
            return;
        }

        //解析协议类型
        LinkType linkType = getLinkType(ctx, in);

        // ===============解析客户端协议包 start =====================
        if (in.readableBytes() < 4) {
            logger.debug("MessageDispatcherDecoder: 可读字节数不够");
            return;
        }

        int length = Integer.reverseBytes(in.readInt());// 接收字节长度
        if (length < 0) {
            throw new CorruptedFrameException("negative length: " + length);
        }

        if (in.readableBytes() < length) {
            //重置ByteBuf 读指针位置
            in.resetReaderIndex();
            return;
        }

        //移除自身处理器
        ctx.pipeline().remove(this);
        //根据连接类型，分发处理器
        dispatcherHandler(ctx, linkType);
        //表示传递消息至下一个处理器
        ctx.fireChannelRead(in);
        // ===============解析客户端协议包 end =====================
    }

    private LinkType getLinkType(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Attribute<LinkType> linkAttr = ctx.channel().attr(NetAttributeKey.LINK_TYPE);

        LinkType linkType = linkAttr.get();
        if (linkType != null) {
            return linkType;
        }

        if (in.isReadable()) {
            int size = in.readableBytes();
            size = Math.min(size, 18);
            byte[] temp = new byte[size];
            in.getBytes(0, temp);
            String str = new String(temp);

            if (StringUtil.isEmpty(str)) {
                return null;
            }

            logger.debug("MessageDispatcherDecoder head: {}", str);

            if (str.toUpperCase().contains("HTTP")) {
                linkType = LinkType.HTTP;
            } else {
                if (str.startsWith(LinkType.TCP.getHead())) {
                    linkType = LinkType.TCP;
                } else if (str.startsWith(LinkType.WEBSOCKET.getHead())) {
                    linkType = LinkType.WEBSOCKET;
                }
            }
        }
        //保存链接类型
        ctx.channel().attr(NetAttributeKey.LINK_TYPE).set(linkType);
        return linkType;
    }

    //TODO: 优化代码逻辑
    private void dispatcherHandler(ChannelHandlerContext ctx, LinkType linkType) {
        if (linkType != null) {
            ChannelPipeline pipeline = ctx.pipeline();
            switch (linkType) {
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
    }

    private void removeHttpHandle(ChannelPipeline pipeline) {
        pipeline.remove("http-codec");
        pipeline.remove("aggregator");
        pipeline.remove("http-chunked");
    }
}
