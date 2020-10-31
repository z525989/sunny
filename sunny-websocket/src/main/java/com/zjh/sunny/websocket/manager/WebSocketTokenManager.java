package com.zjh.sunny.websocket.manager;

import com.zjh.sunny.core.util.StringUtil;
import com.zjh.sunny.websocket.WebSocketProperties;
import com.zjh.sunny.websocket.constant.DefaultConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author zhangJinHui
 * @date 2020/10/31 12:21
 */
@Component
public class WebSocketTokenManager {

    @Autowired
    private WebSocketProperties webSocketProperties;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public String genToken(long userId) {
        String token    = userId + ":" + StringUtil.getUUID();

        String cacheKey = webSocketProperties.getTokenCacheKeyPrefix();
        if (StringUtil.isEmpty(cacheKey)) {
            cacheKey = DefaultConstant.DEFAULT_TOKEN_KEY_PREFIX;
        }

        cacheKey += userId;

        long expire = webSocketProperties.getTokenCacheExpire();

        if (expire == -1) {
            redisTemplate.opsForValue().set(cacheKey, token);
        } else {
            if (expire <= 0) {
                expire = DefaultConstant.DEFAULT_TOKEN_EXPIRE;
            }

            redisTemplate.opsForValue().set(cacheKey, token, expire, TimeUnit.MILLISECONDS);
        }

        return token;
    }

    public String genToken(String uid) {
        String token    = uid + ":" + StringUtil.getUUID();

        String cacheKey = webSocketProperties.getTokenCacheKeyPrefix();
        if (StringUtil.isEmpty(cacheKey)) {
            cacheKey = DefaultConstant.DEFAULT_TOKEN_KEY_PREFIX;
        }

        cacheKey += uid;

        long expire = webSocketProperties.getTokenCacheExpire();

        if (expire == -1) {
            redisTemplate.opsForValue().set(cacheKey, token);
        } else {
            if (expire <= 0) {
                expire = DefaultConstant.DEFAULT_TOKEN_EXPIRE;
            }

            redisTemplate.opsForValue().set(cacheKey, token, expire, TimeUnit.MILLISECONDS);
        }

        return token;
    }

    public boolean verityToken(String token) {
        if (StringUtil.isEmpty(token)) {
            return false;
        }

        if (!token.contains(":")) {
            return false;
        }

        String cacheKey = webSocketProperties.getTokenCacheKeyPrefix();
        if (StringUtil.isEmpty(cacheKey)) {
            cacheKey = DefaultConstant.DEFAULT_TOKEN_KEY_PREFIX;
        }

        String uid = token.split(":")[0];
        cacheKey += uid;

        String cacheStr = redisTemplate.opsForValue().get(cacheKey);

        return token.equals(cacheStr);
    }

    public boolean verityAndExpireToken(String token) {
        if (StringUtil.isEmpty(token)) {
            return false;
        }

        if (!token.contains(":")) {
            return false;
        }

        String cacheKey = webSocketProperties.getTokenCacheKeyPrefix();
        if (StringUtil.isEmpty(cacheKey)) {
            cacheKey = DefaultConstant.DEFAULT_TOKEN_KEY_PREFIX;
        }

        String uid = token.split(":")[0];
        cacheKey += uid;

        String cacheStr = redisTemplate.opsForValue().get(cacheKey);

        if (!token.equals(cacheStr)) {
            return false;
        }

        long expire = webSocketProperties.getTokenCacheExpire();

        if (expire == 0) {
            expire = DefaultConstant.DEFAULT_TOKEN_EXPIRE;
            redisTemplate.opsForValue().set(cacheKey, token, expire, TimeUnit.MILLISECONDS);
        }

        return true;
    }

    public boolean deleteToken(String token) {
        if (StringUtil.isEmpty(token)) {
            return false;
        }

        if (!token.contains(":")) {
            return false;
        }

        String cacheKey = webSocketProperties.getTokenCacheKeyPrefix();
        if (StringUtil.isEmpty(cacheKey)) {
            cacheKey = DefaultConstant.DEFAULT_TOKEN_KEY_PREFIX;
        }

        String uid = token.split(":")[0];
        cacheKey += uid;

        Boolean result  = redisTemplate.delete(cacheKey);

        return result != null ? result : false;
    }

    public boolean deleteTokenByUid(String uid) {
        if (StringUtil.isEmpty(uid)) {
            return false;
        }

        String cacheKey = webSocketProperties.getTokenCacheKeyPrefix();
        if (StringUtil.isEmpty(cacheKey)) {
            cacheKey = DefaultConstant.DEFAULT_TOKEN_KEY_PREFIX;
        }

        cacheKey += uid;

        Boolean result  = redisTemplate.delete(cacheKey);

        return result != null ? result : false;
    }

    public boolean deleteTokenByUserId(long userId) {
        if (userId == 0) {
            return false;
        }

        String cacheKey = webSocketProperties.getTokenCacheKeyPrefix();
        if (StringUtil.isEmpty(cacheKey)) {
            cacheKey = DefaultConstant.DEFAULT_TOKEN_KEY_PREFIX;
        }

        cacheKey += userId;

        Boolean result  = redisTemplate.delete(cacheKey);

        return result != null ? result : false;
    }

}
