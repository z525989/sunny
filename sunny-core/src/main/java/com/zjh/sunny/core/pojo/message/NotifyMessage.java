package com.zjh.sunny.core.pojo.message;


/**
 * @author zhangJinHui
 * @date 2020-3-20 10:03
 */
public class NotifyMessage {

    //上线的通知
    public static final int SESSION_ON = 1;

    //下线的通知
    public static final int SESSION_OFF = 2;

    //消息转发
    public static final int FORWARD = 3;

    private int type;

    private Object message;

    private long userId;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
