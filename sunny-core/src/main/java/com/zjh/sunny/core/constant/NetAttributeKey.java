package com.zjh.sunny.core.constant;

import io.netty.util.AttributeKey;

/**
 * @author zhangJinHui
 * @date 2020/3/22 13:29
 */
public class NetAttributeKey {

    /**
     * 连接类型
     */
    public static final AttributeKey<NetType> LINK_TYPE = AttributeKey.valueOf("LinkType");

    /**
     * 用户id
     */
    public static final AttributeKey<Long> USER_ID = AttributeKey.valueOf("userId");

    /**
     * sessionId
     */
    public static final AttributeKey<String> SESSION_ID = AttributeKey.valueOf("sessionId");

}
