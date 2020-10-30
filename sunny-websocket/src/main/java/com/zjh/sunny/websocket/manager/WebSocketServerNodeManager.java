package com.zjh.sunny.websocket.manager;

import com.alibaba.fastjson.JSONObject;
import com.zjh.sunny.core.pojo.message.NotifyMessage;
import com.zjh.sunny.core.pojo.node.NettyServerNode;
import com.zjh.sunny.core.registry.RegistryCenter;
import com.zjh.sunny.websocket.WebSocketProperties;
import com.zjh.sunny.websocket.node.NodeSender;
import com.zjh.sunny.websocket.session.WebSocketSession;
import io.netty.channel.ChannelHandlerContext;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * websocket 节点管理
 * @author zhangJinHui
 * @date 2020-3-12 15:36
 */
@Component
public class WebSocketServerNodeManager {

    private final Logger logger = LoggerFactory.getLogger(WebSocketServerNodeManager.class);

    @Autowired
    private RegistryCenter registryCenter;

    @Autowired
    private WebSocketProperties webSocketProperties;

    @Autowired
    private WebSocketPushManager webSocketPushManager;

    /**
     * 注册到netty的节点（带临时id）
     */
    private String registerPath;

    /**
     * 当前服务器节点信息
     */
    private final NettyServerNode localNode = new NettyServerNode();

    /**
     * 节点通知器Map
     */
    private final Map<Long, NodeSender> nodeSenderMap = new ConcurrentHashMap<>();

    /**
     * 初始化节点信息
     * 注册节点到zookeeper
     */
    public void initNode() {
        try {
            int port          = webSocketProperties.getPort();
            String host       = webSocketProperties.getHost();
            String parentNode = webSocketProperties.getZkParentNode();
            String pathPrefix = webSocketProperties.getZkPathPrefix();

            //保存netty连接信息
            localNode.setHost(host);
            localNode.setPort(port);

            //创建父节点
            registryCenter.createParentIfNeeded(parentNode);

            //注册临时节点
            String nodePatch = parentNode + pathPrefix;

            registerPath = registryCenter.createNode(
                    nodePatch,
                    CreateMode.EPHEMERAL_SEQUENTIAL,
                    JSONObject.toJSONBytes(localNode));

            logger.info("initNode registerPath : {}", registerPath);

            long nodeId = getLocalNodeId();

            //初始化节点信息
            localNode.setId(nodeId);

            //添加节点监听器
            addListener(parentNode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addListener(String parentNode) {
        //订阅节点的增加和删除事件
        PathChildrenCacheListener childrenCacheListener = (client, event) -> {
            ChildData data = event.getData();
            switch (event.getType()) {
                case CHILD_ADDED:
                    logger.info("======= 增加节点：{} | 数据: {}", data.getPath(), new String(data.getData()));
                    addNodeProcess(data);
                    break;
                case CHILD_REMOVED:
                    logger.info("======= 移除节点：{} | 数据: {}", data.getPath(), new String(data.getData()));
                    removeNodeProcess(data);
                    break;
                case CHILD_UPDATED:
                    logger.info("======= 更新节点：{} | 数据: {}", data.getPath(), new String(data.getData()));
                    break;
                default:
                    break;
            }
        };
        registryCenter.addChildrenCacheListener(parentNode, childrenCacheListener);
    }

    /**
     * 增加客户端
     */
    private void addNodeProcess(ChildData data) {
        try {
            byte[] payload = data.getData();
            NettyServerNode remoteNode = JSONObject.parseObject(payload, NettyServerNode.class);

            long remoteNodeId = getIdByPath(data.getPath());
            remoteNode.setId(remoteNodeId);

            if (remoteNode.equals(localNode)) {
                logger.info("======= 增加节点：当前节点为本地节点 ");
                return;
            }

            NodeSender nodeSender = nodeSenderMap.get(remoteNodeId);
            if (nodeSender != null && nodeSender.getNode().equals(remoteNode)) {
                logger.info("======= 增加节点：节点已经存在 ");
                return;
            }

            //节点信息更新
            if (nodeSender != null) {
                //关闭老的连接
                nodeSender.disConnect();
            }

            nodeSender = new NodeSender(remoteNode);

            //连接到对应节点
            nodeSender.doConnect();

            nodeSenderMap.put(remoteNodeId, nodeSender);
        } catch (Exception e) {
            logger.error("addNodeProcess error :", e);
        }
    }

    /**
     * 客户端下线
     */
    private void removeNodeProcess(ChildData data) {
        try {
            byte[] payload = data.getData();
            NettyServerNode remoteNode = JSONObject.parseObject(payload, NettyServerNode.class);

            long remoteNodeId = getIdByPath(data.getPath());
            remoteNode.setId(remoteNodeId);

            NodeSender nodeSender = nodeSenderMap.get(remoteNodeId);
            if (null != nodeSender) {
                //断开对应节点连接
                nodeSender.disConnect();
                nodeSenderMap.remove(remoteNodeId);
            }
        } catch (Exception e) {
            logger.error("removeNodeProcess error :", e);
        }
    }

    /**
     * 获取本地节点信息
     */
    public NettyServerNode getLocalNode() {
        return localNode;
    }

    /**
     * 判断节点是否为本地节点
     */
    public boolean isLocal(NettyServerNode localNode) {
        return this.localNode.equals(localNode);
    }

    /**
     * 获取本地节点分布式id
     */
    public long getLocalNodeId() {
        return getIdByPath(registerPath);
    }

    /**
     * 获取节点分布式id
     * @param nodePath 注册节点路径
     * @return 注册节点分布式id
     */
    public long getIdByPath(String nodePath) {
        String sid = null;
        if (null == nodePath) {
            throw new RuntimeException("节点注册失败");
        }

        String parentNode = webSocketProperties.getZkParentNode();
        String pathPrefix = webSocketProperties.getZkPathPrefix();

        String nodeStr = parentNode + pathPrefix;

        int index = nodePath.lastIndexOf(nodeStr);
        if (index >= 0) {
            index += nodeStr.length();
            sid = index <= nodePath.length() ? nodePath.substring(index) : null;
        }

        if (null == sid) {
            throw new RuntimeException("节点ID生成失败");
        }

        return Long.parseLong(sid);
    }

    /**
     * 接收转发消息内容
     */
//    @Async("asyncWebsocket")
    public void receiveNotify(ChannelHandlerContext ctx, NotifyMessage message) {
        try {
            switch (message.getType()) {
                case NotifyMessage.SESSION_ON:
                    logger.info("======= 节点上线，节点ip:{}", ctx.channel().remoteAddress());
                    break;
                case NotifyMessage.SESSION_OFF:
                    logger.info("======= 节点下线，节点ip:{}", ctx.channel().remoteAddress());
                    break;
                case NotifyMessage.FORWARD:
                    receiveForwardMsg(message);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            logger.error("receiveNotify error :", e);
        }
    }

    /**
     * 接收转发消息
     */
    private void receiveForwardMsg(NotifyMessage message) {
        long userId = message.getUserId();
        Object msg = message.getMessage();

        webSocketPushManager.sendWsMsgToUser(userId, msg);
    }

    /**
     * 转发消息到对应节点
     * @param session session
     * @param msg 消息内容
     */
    public void forwardMsgToRemoteNode(WebSocketSession session, Object msg) {
        // 1. 获取用户所在节点
        NettyServerNode nettyServerNode = session.getNode();
        if (nettyServerNode == null) {
            return;
        }

        NodeSender nodeSender = nodeSenderMap.get(nettyServerNode.getId());
        if (nodeSender == null) {
            return;
        }

        // 2. 封装消息
        NotifyMessage notifyMessage = new NotifyMessage();

        notifyMessage.setType(NotifyMessage.FORWARD);
        notifyMessage.setMessage(msg);
        notifyMessage.setUserId(session.getUserId());

        // 3. 发送消息到对应节点
        nodeSender.writeAndFlush(notifyMessage);
    }


    //TODO: 节点负载均衡
//    /**
//     * 增加负载，表示有用户登录成功
//     *
//     * @return 成功状态
//     */
//    public boolean incBalance() {
//        if (null == nettyServerNode) {
//            throw new RuntimeException("还没有设置Node 节点");
//        }
//        // 增加负载：增加负载，并写回zookeeper
//        try {
//            nettyServerNode.incrementBalance();
//
//            client.setData().forPath(pathRegistered, payload);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    /**
//     * 减少负载，表示有用户下线，写回zookeeper
//     *
//     * @return 成功状态
//     */
//    public boolean decrBalance() {
//        if (null == localNode) {
//            throw new RuntimeException("还没有设置Node 节点");
//        }
//        try {
//
//            localNode.decrementBalance();
//
//            byte[] payload = JsonUtil.object2JsonBytes(localNode);
//            client.setData().forPath(pathRegistered, payload);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
}
