package com.zjh.sunny.rpc;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sunny.rpc")
public class RpcProperties {

    /**
     * rpc端口号
     */
    private int port;


    /**
     * 请求队列容量
     */
    private int requestQueueSize;
}
