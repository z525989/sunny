package com.zjh.sunny.core.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sunny.websocket")
public class WebsocketProperties {

    /**
     * websocket端口号
     */
    private int port;


}
