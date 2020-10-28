package com.zjh.sunny.websocket;

import com.zjh.sunny.core.util.StringUtil;
import com.zjh.sunny.websocket.node.WebSocketServerNodeManager;
import com.zjh.sunny.core.zookeeper.ZookeeperRegistryCenter;
import com.zjh.sunny.websocket.mapping.WebSocketMappingBindHandler;
import com.zjh.sunny.websocket.server.NettyWebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * websocket 启动器
 * @author zhangJinHui
 * @date 2020年7月16日 10:39:06
 */
@Component
public class NettyWebSocketBoot {

    private final Logger logger = LoggerFactory.getLogger(NettyWebSocketBoot.class);

    @Autowired
    private NettyWebSocketServer nettyWebSocketServer;

    @Autowired
    private WebSocketProperties webSocketProperties;

    @Autowired
    private WebSocketMappingBindHandler webSocketMappingBindHandler;

    @Autowired
    private ZookeeperRegistryCenter zookeeperRegistryCenter;

    @Autowired
    private WebSocketServerNodeManager webSocketServerNodeManager;

    public void run() {
        logger.info("---------------- Netty WebSocket Server 开始启动 ----------------");

        int websocketPort     = webSocketProperties.getPort();
        String apiBasePackage = webSocketProperties.getApiBasePackage();

        nettyWebSocketServer.setPort(websocketPort);

        // 1.启动netty服务
        Thread thread = new Thread(nettyWebSocketServer);
        thread.start();

        if (StringUtil.isBlank(apiBasePackage)) {
            throw new NullPointerException("端口绑定包名为空");
        }

        // 2.扫描并绑定webSocket api mapping
        webSocketMappingBindHandler.bind(apiBasePackage);

        // 3.初始化zookeeper连接
        zookeeperRegistryCenter.init();

        // 4.初始化zookeeper节点
        webSocketServerNodeManager.initNode();

        logger.info("---------------- Netty WebSocket Server 启动结束 ----------------");
    }

}
