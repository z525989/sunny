package com.zjh.sunny.websocket.dao.impl;

import com.alibaba.fastjson.JSONObject;
import com.zjh.sunny.core.util.StringUtil;
import com.zjh.sunny.websocket.dao.WebSocketSessionDao;
import com.zjh.sunny.websocket.session.WebSocketSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class WebSocketSessionRedisDaoImpl implements WebSocketSessionDao {

    public static final int TIMEOUT = 30 * 60 * 1000;

    public static final String CACHE_KEY = "ws:session:uid:";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public WebSocketSession getSessionByUserId(long userId) {
        String cacheKey = CACHE_KEY + userId;
        String cacheStr = redisTemplate.opsForValue().get(cacheKey);

        if (StringUtil.isEmpty(cacheStr)) {
            return null;
        }

        return JSONObject.parseObject(cacheStr, WebSocketSession.class);
    }

    @Override
    public void saveSession(WebSocketSession webSocketSession) {
        String cacheKey = CACHE_KEY + webSocketSession.getUserId();
        String cacheStr = JSONObject.toJSONString(webSocketSession);
        redisTemplate.opsForValue().set(cacheKey, cacheStr, TIMEOUT, TimeUnit.MILLISECONDS);
    }

    @Override
    public void deleteSessionByUserId(long userId) {
        String cacheKey = CACHE_KEY + userId;
        redisTemplate.delete(cacheKey);
    }
}
