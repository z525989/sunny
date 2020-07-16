package com.zjh.sunny.websocket;

import com.zjh.sunny.core.util.StringUtil;
import com.zjh.sunny.websocket.mapping.MappingBindHandler;
import com.zjh.sunny.websocket.server.NettyServer;
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
    private NettyServer nettyServer;

    @Autowired
    private MappingBindHandler mappingBindHandler;

//    @Autowired
//    private ZookeeperRegistryCenter zookeeperRegistryCenter;

//    @Autowired
//    private ServerNodeManager serverNodeManager;

    public void run(String projectPackage) {
        logger.info("---------------- Netty WebSocket Server 开始启动 ----------------");

        Thread thread = new Thread(nettyServer);
        thread.start();

        if (StringUtil.isBlank(projectPackage)) {
            throw new NullPointerException("端口绑定包名为空");
        }

        mappingBindHandler.bind(projectPackage);

        //初始化zookeeper
//        zookeeperRegistryCenter.init();

        //初始化zookeeper节点
//        serverNodeManager.initNode();

        logger.info("---------------- Netty WebSocket Server 启动结束 ----------------");
    }

}
