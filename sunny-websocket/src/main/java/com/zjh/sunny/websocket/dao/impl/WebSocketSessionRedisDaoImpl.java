package com.zjh.sunny.websocket.dao.impl;

import com.alibaba.fastjson.JSONObject;
import com.zjh.sunny.core.util.StringUtil;
import com.zjh.sunny.websocket.WebSocketProperties;
import com.zjh.sunny.websocket.constant.DefaultConstant;
import com.zjh.sunny.websocket.dao.WebSocketSessionDao;
import com.zjh.sunny.websocket.session.WebSocketSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class WebSocketSessionRedisDaoImpl implements WebSocketSessionDao {

    @Autowired
    private WebSocketProperties webSocketProperties;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public WebSocketSession getSessionByUserId(long userId) {
        String cacheKey = webSocketProperties.getTokenCacheKeyPrefix();
        if (StringUtil.isEmpty(cacheKey)) {
            cacheKey = DefaultConstant.DEFAULT_SESSION_KEY_PREFIX;
        }

        cacheKey += userId;

        String cacheStr = redisTemplate.opsForValue().get(cacheKey);

        if (StringUtil.isEmpty(cacheStr)) {
            return null;
        }

        return JSONObject.parseObject(cacheStr, WebSocketSession.class);
    }

    @Override
    public void saveSession(WebSocketSession webSocketSession) {
        String cacheKey = webSocketProperties.getTokenCacheKeyPrefix();
        if (StringUtil.isEmpty(cacheKey)) {
            cacheKey = DefaultConstant.DEFAULT_SESSION_KEY_PREFIX;
        }

        cacheKey += webSocketSession.getUserId();

        String cacheStr = JSONObject.toJSONString(webSocketSession);

        long expire = webSocketProperties.getTokenCacheExpire();

        if (expire == -1) {
            redisTemplate.opsForValue().set(cacheKey, cacheStr);
        } else {
            if (expire <= 0) {
                expire = DefaultConstant.DEFAULT_SESSION_EXPIRE;
            }
            redisTemplate.opsForValue().set(cacheKey, cacheStr, expire, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void deleteSessionByUserId(long userId) {
        String cacheKey = webSocketProperties.getTokenCacheKeyPrefix();
        if (StringUtil.isEmpty(cacheKey)) {
            cacheKey = DefaultConstant.DEFAULT_SESSION_KEY_PREFIX;
        }
        redisTemplate.delete(cacheKey);
    }
}
