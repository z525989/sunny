package com.zjh.sunny.core.pojo.node;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author zhangJinHui
 * @date 2020-3-12 15:34
 */
public class NettyServerNode implements Serializable {

    private static final long serialVersionUID = 4566252085064589137L;

    /**
     * 节点id
     */
    private long id;

    /**
     * netty host
     */
    private String host;

    /**
     * netty port
     */
    private int port;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NettyServerNode node = (NettyServerNode) o;
        return Objects.equals(host, node.host) &&
                Objects.equals(port, node.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, host, port);
    }

    @Override
    public String toString() {
        return "NettyServerNode {id=" + id + ",host=" + host + ",port=" + port + "}";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
