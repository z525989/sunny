package com.zjh.sunny.websocket;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sunny.websocket")
public class WebSocketProperties {

    /**
     * websocket端口号
     */
    private int port;

    /**
     * 节点ip/域名
     */
    private String host;

    /**
     * websocket接口所在包名
     */
    private String apiBasePackage;

    /**
     * websocket server zookeeper 注册 根节点
     */
    private String zkParentNode;

    /**
     * websocket server zookeeper 注册 子节点
     */
    private String zkPathPrefix;

    /**
     * token缓存key前缀
     */
    private String tokenCacheKeyPrefix;

    /**
     * token缓存过期时间(毫秒)
     * -1: 不过期
     */
    private long tokenCacheExpire;

    /**
     * session缓存key前缀
     */
    private String sessionCacheKeyPrefix;

    /**
     * session缓存过期时间(毫秒)
     * -1: 不过期
     */
    private long sessionCacheExpire;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getApiBasePackage() {
        return apiBasePackage;
    }

    public void setApiBasePackage(String apiBasePackage) {
        this.apiBasePackage = apiBasePackage;
    }

    public String getZkParentNode() {
        return zkParentNode;
    }

    public void setZkParentNode(String zkParentNode) {
        this.zkParentNode = zkParentNode;
    }

    public String getZkPathPrefix() {
        return zkPathPrefix;
    }

    public void setZkPathPrefix(String zkPathPrefix) {
        this.zkPathPrefix = zkPathPrefix;
    }

    public String getTokenCacheKeyPrefix() {
        return tokenCacheKeyPrefix;
    }

    public void setTokenCacheKeyPrefix(String tokenCacheKeyPrefix) {
        this.tokenCacheKeyPrefix = tokenCacheKeyPrefix;
    }

    public long getTokenCacheExpire() {
        return tokenCacheExpire;
    }

    public void setTokenCacheExpire(long tokenCacheExpire) {
        this.tokenCacheExpire = tokenCacheExpire;
    }

    public String getSessionCacheKeyPrefix() {
        return sessionCacheKeyPrefix;
    }

    public void setSessionCacheKeyPrefix(String sessionCacheKeyPrefix) {
        this.sessionCacheKeyPrefix = sessionCacheKeyPrefix;
    }

    public long getSessionCacheExpire() {
        return sessionCacheExpire;
    }

    public void setSessionCacheExpire(long sessionCacheExpire) {
        this.sessionCacheExpire = sessionCacheExpire;
    }
}
