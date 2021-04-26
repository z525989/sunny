package com.zjh.sunny.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangJinHui
 * @date 2021/4/26 10:18
 */
@Configuration
public class SunnyAuthConfiguration implements CommandLineRunner {

    @Autowired
    private NettyWebSocketBoot nettyWebSocketBoot;

    @Override
    public void run(String... args) throws Exception {
        nettyWebSocketBoot.run();
    }
}
