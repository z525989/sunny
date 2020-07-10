package com.zjh.sunny.zookeeper.base;

import java.io.Serializable;

/**
 * @author zhangJinHui
 * @date 2020-3-12 11:45
 */
public class ZkNodeInfo implements Serializable {

    /**
     * serializable id
     */
    private static final long serialVersionUID = -7093359804202316616L;

    /**
     * 节点id
     */
    private long id;

    /**
     * 节点名称
     */
    private String name;

    /**
     * 节点地址
     */
    private String host;

    /**
     * 节点端口号
     */
    private int port;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
