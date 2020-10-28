package com.zjh.sunny.core.pojo.message;

/**
 * @author zhangJinHui
 * @date 2020/3/16 21:41
 */
public class WebSocketMessageType {

    /**
     * RPC消息
     */
    public static final int RPC = 1;

    /**
     * 服务之前通知
     */
    public static final int NOTIFY = 2;

    /**
     * 客户端请求
     */
    public static final int CLIENT = 3;
}
