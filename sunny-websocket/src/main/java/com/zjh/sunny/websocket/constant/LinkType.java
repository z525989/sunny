package com.zjh.sunny.websocket.constant;

/**
 * @author zhangJinHui
 * @date 2020/3/22 13:30
 */
public enum LinkType {

    HTTP(1),

    WEBSOCKET(2);

    LinkType(int type) {
        this.type = type;
    }

    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
