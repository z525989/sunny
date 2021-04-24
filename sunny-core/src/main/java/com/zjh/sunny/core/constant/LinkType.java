package com.zjh.sunny.core.constant;

/**
 * netty 连接类型
 * @author zhangJinHui
 * @date 2020/3/22 13:30
 */
public enum LinkType {

    HTTP(1, "HTTP"),

    WEBSOCKET(2, "WEBSOCKET:"),

    TCP(3, "TCP:");

    private int type;

    private String head;

    LinkType(int type, String head) {
        this.type = type;
        this.head = head;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }
}
