package com.zjh.sunny.websocket.constant;

import com.zjh.sunny.websocket.session.WebSocketSession;
import io.netty.util.AttributeKey;

public class WebSocketAttributeKey {

    /**
     *
     */
    public static final AttributeKey<WebSocketSession> SESSION = AttributeKey.valueOf("websocket-session");
}
