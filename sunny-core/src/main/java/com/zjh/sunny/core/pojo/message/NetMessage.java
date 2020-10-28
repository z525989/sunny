package com.zjh.sunny.core.pojo.message;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * socket请求包
 * @author zhangJinHui
 */
public class NetMessage implements Serializable {

    private static final long serialVersionUID = -2258073051347458378L;

    /**
     * 消息类型
     * @see WebSocketMessageType
     */
    private int type;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * token
     */
    private String token;

    /**
     * 协议码
     */
    private int protocolCode;

    /**
     * 错误码
     */
    private int code;

    /**
     * 消息描述
     */
    private String msg;

    /**
     * 数据
     */
    private JSONObject data;

    public NetMessage() {
        this.data = new JSONObject();
    }

    public NetMessage(int code, String msg) {
        this.data = new JSONObject();
        this.code = code;
        this.msg = msg;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getProtocolCode() {
        return protocolCode;
    }

    public void setProtocolCode(int protocolCode) {
        this.protocolCode = protocolCode;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }
}
