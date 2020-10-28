package com.zjh.sunny.websocket.session;

import com.zjh.sunny.core.pojo.node.NettyServerNode;

/**
 * netty local session
 * @author zhangJinHui
 * @date 2019/11/23 23:41
 */
public class WebSocketSession {

    /**
     * 用户id
     */
    private long userId;

    /**
     * netty sessionId
     */
    private String sessionId;

    /**
     * 所在服务器节点信息
     */
    private NettyServerNode node;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public NettyServerNode getNode() {
        return node;
    }

    public void setNode(NettyServerNode node) {
        this.node = node;
    }
}
