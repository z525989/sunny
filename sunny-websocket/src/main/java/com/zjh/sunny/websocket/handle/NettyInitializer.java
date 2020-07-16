package com.zjh.sunny.websocket.handle;

import com.zjh.sunny.websocket.coder.MessageDispatcherDecoder;
import com.zjh.sunny.websocket.coder.WebSocketMessageDecoder;
import com.zjh.sunny.websocket.coder.WebSocketMessageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NettyInitializer extends ChannelInitializer<NioSocketChannel> {

    @Autowired
    private WebSocketHandler webSocketHandler;

    @Override
    protected void initChannel(NioSocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();

        pipeline.addLast("messageDispatcherDecoder", new MessageDispatcherDecoder());

        //HTTP编码解码器
        pipeline.addLast("http-codec", new HttpServerCodec());
        //HTTP 消息的合并处理 把HTTP头、HTTP体拼成完整的HTTP请求
        pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
        //分块，方便大文件传输，不过实质上都是短的文本数据
        pipeline.addLast("http-chunked", new ChunkedWriteHandler());

        //自定义解码器
        pipeline.addLast("webSocketMessageDecoder", new WebSocketMessageDecoder());

        //websocket消息处理
        pipeline.addLast(new WebSocketMessageEncoder());

        pipeline.addLast("webSocketHandler", webSocketHandler);
    }
}
