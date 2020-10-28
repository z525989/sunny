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
     * websocket接口所在包名
     */
    private String wsApiBasePackage;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getWsApiBasePackage() {
        return wsApiBasePackage;
    }

    public void setWsApiBasePackage(String wsApiBasePackage) {
        this.wsApiBasePackage = wsApiBasePackage;
    }
}
