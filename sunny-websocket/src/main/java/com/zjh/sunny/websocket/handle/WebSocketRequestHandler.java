package com.zjh.sunny.websocket.handle;

import com.alibaba.fastjson.JSONObject;
import com.zjh.sunny.core.util.SpringBeanUtil;
import com.zjh.sunny.core.constant.ErrorCode;
import com.zjh.sunny.websocket.mapping.WebSocketMappingBindHandler;
import com.zjh.sunny.websocket.mapping.WebSocketMapping;
import com.zjh.sunny.websocket.message.WsMessage;
import com.zjh.sunny.websocket.session.SessionManager;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;

/**
 * @author zhangJinHui
 * @date 2019/11/23 23:42
 */
@Service
public class WebSocketRequestHandler {

    private final Logger logger = LoggerFactory.getLogger(WebSocketRequestHandler.class);

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private WebSocketMappingBindHandler webSocketMappingBindHandler;

    /**
     * netty webSocket 异步业务
     *
     * @param ctx netty 通道
     *
     * @param requestData 请求数据
     */
    @Async("asyncWebsocket")
    public void executor(ChannelHandlerContext ctx, WsMessage requestData) {
        logger.debug("======= socket请求数据: " + JSONObject.toJSONString(requestData));
        try {
            int protocolCode = requestData.getProtocolCode();

            Method method = webSocketMappingBindHandler.getMethod(protocolCode);
            if (method == null) {
                ctx.channel().writeAndFlush(new WsMessage(ErrorCode.ERROR, "请求协议不存在！"));
                return;
            }

            WebSocketMapping requestMapper = method.getAnnotation(WebSocketMapping.class);

            boolean isAuth = requestMapper.isAuth();
            if (isAuth) {
                //TODO:token校验
                String token = requestData.getToken();
            }

            Object clazz = SpringBeanUtil.getBean(method.getDeclaringClass());

            Object obj = method.invoke(clazz, requestData);

            if (obj instanceof WsMessage) {
                ctx.channel().writeAndFlush(obj);
            }
        } catch (Exception e) {
            logger.error("netty request error:", e);
            ctx.channel().writeAndFlush(new WsMessage(ErrorCode.ERROR, "asyncWebsocket error"));
        }
    }

    private void invokeProcess() {

    }
}
