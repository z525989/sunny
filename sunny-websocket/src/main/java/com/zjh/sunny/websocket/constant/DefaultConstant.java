package com.zjh.sunny.websocket.constant;

/**
 * 默认配置
 * @author zhangJinHui
 * @date 2020/10/31 14:21
 */
public class DefaultConstant {

    /**
     * token缓存默认过期时间
     */
    public static final int DEFAULT_TOKEN_EXPIRE = 30 * 60 * 1000;

    /**
     * session缓存默认过期时间
     */
    public static final int DEFAULT_SESSION_EXPIRE = 30 * 60 * 1000;

    /**
     * token缓存默认Key
     */
    public static final String DEFAULT_TOKEN_KEY_PREFIX = "sunny:ws:token:";

    /**
     * session缓存默认Key
     */
    public static final String DEFAULT_SESSION_KEY_PREFIX = "sunny:ws:session:";
}
