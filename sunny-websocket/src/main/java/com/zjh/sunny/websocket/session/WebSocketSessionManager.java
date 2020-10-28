package com.zjh.sunny.websocket.session;

import com.zjh.sunny.core.constant.NetAttributeKey;
import com.zjh.sunny.core.pojo.node.NettyServerNode;
import com.zjh.sunny.core.util.StringUtil;
import com.zjh.sunny.websocket.constant.WebSocketAttributeKey;
import com.zjh.sunny.websocket.dao.WebSocketSessionDao;
import com.zjh.sunny.websocket.node.WebSocketServerNodeManager;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * 1.用户登录成功后，保存节点信息到session
 * 2.session 保存到redis
 * 3.根据心跳延长redis缓存
 * 4.根据session里面节点信息转发对应消息到指定服务器
 *
 * netty会话管理
 * @author zhangJinHui
 * @date 2019/8/10 19:07
 */
@Component
public class WebSocketSessionManager {

    private final ChannelGroup channelGroup         = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private final Map<String, Channel> channelMap   = new ConcurrentHashMap<>();

    private final Map<Long, Channel> userChannelMap = new ConcurrentHashMap<>();

    @Autowired
    private WebSocketSessionDao webSocketSessionDao;

    @Autowired
    private WebSocketServerNodeManager webSocketServerNodeManager;

    public void addChannel(Channel channel) {
        String sessionId = getSessionId(channel);
        channel.attr(NetAttributeKey.SESSION_ID).set(sessionId);

        channelGroup.add(channel);
        channelMap.put(sessionId, channel);
    }

    public void removeChannel(Channel channel) {
        channelGroup.remove(channel);
        Long userId = channel.attr(NetAttributeKey.USER_ID).get();
        if (userId != null) {
            userChannelMap.remove(userId);
        }
        String sessionId = channel.attr(NetAttributeKey.SESSION_ID).get();
        if (sessionId != null) {
            channelMap.remove(sessionId);
        }
    }

    public ChannelGroup getChannelGroup() {
        return this.channelGroup;
    }

    public void bindUserSession(String sessionId, long userId) {
        NettyServerNode localNode = webSocketServerNodeManager.getLocalNode();

        Channel channel = channelMap.get(sessionId);

        WebSocketSession session = new WebSocketSession();

        session.setUserId(userId);
        session.setSessionId(sessionId);
        session.setNode(localNode);

        channel.attr(NetAttributeKey.USER_ID).set(userId);
        channel.attr(NetAttributeKey.SESSION_ID).set(sessionId);
        userChannelMap.put(userId, channel);

        webSocketSessionDao.saveSession(session);
    }

    public String getSessionId(Channel channel) {
        return channel.id().asLongText();
    }

    public Channel getChannelByUserId(long userId) {
        return userChannelMap.get(userId);
    }

    public Channel getChannelBySessionId(String sessionId) {
        return channelMap.get(sessionId);
    }

    public boolean checkSession(Channel channel, String sessionId) {
        String channelSessionId = channel.attr(NetAttributeKey.SESSION_ID).get();

        if (StringUtil.isEmpty(channelSessionId) || StringUtil.isEmpty(sessionId)) {
            return false;
        }

        if (!channelSessionId.endsWith(sessionId)) {
            return false;
        }

        //TODO： redis缓存数据校验

        return true;
    }

    public void expireSession(long userId) {

    }

    public WebSocketSession getWebSocketSessionByUserId(long userId) {
        return webSocketSessionDao.getSessionByUserId(userId);
    }

}
