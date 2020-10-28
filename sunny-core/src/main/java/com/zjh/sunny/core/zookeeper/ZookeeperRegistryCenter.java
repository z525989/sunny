package com.zjh.sunny.core.zookeeper;

import com.zjh.sunny.core.registry.RegistryCenter;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhangJinHui
 * @date 2020-3-12 10:57
 */
@Service
public class ZookeeperRegistryCenter implements RegistryCenter {

    private static final Logger logger = LoggerFactory.getLogger(ZookeeperRegistryCenter.class);

    @Autowired
    private ZookeeperProperties zookeeperProperties;

    private CuratorFramework client = null ;

    @Override
    public void init() {
        int baseSleepTimeMs =   zookeeperProperties.getBaseSleepTimeMs();
        int maxRetry        =   zookeeperProperties.getMaxRetry();
        String digest       =   zookeeperProperties.getDigest();

        //重试策略
        RetryPolicy policy = new ExponentialBackoffRetry(baseSleepTimeMs, maxRetry);

        //通过工厂创建Curator
        CuratorFrameworkFactory.Builder factory = CuratorFrameworkFactory.builder();

        factory.connectString(zookeeperProperties.getServer());
        factory.connectionTimeoutMs(zookeeperProperties.getConnectionTimeoutMs());
        factory.sessionTimeoutMs(zookeeperProperties.getSessionTimeoutMs());
        factory.retryPolicy(policy);

        if (digest != null) {
            factory.authorization("digest", digest.getBytes());
        }

        client = factory.build();

        //开启连接
        client.start();

        logger.info("zookeeper 初始化完成...");
    }

    @Override
    public void close() {
        if (client != null) {
            client.close();
        }
    }

    @Override
    public void createParentIfNeeded(String managePath) {
        try {
            Stat stat = client.checkExists().forPath(managePath);
            if (null == stat) {
                client.create()
                        .creatingParentsIfNeeded()
                        .withProtection()
                        .withMode(CreateMode.PERSISTENT)
                        .forPath(managePath);
            }
        } catch (Exception e) {
            logger.error("createParentIfNeeded error :", e);
        }
    }

    @Override
    public String createNode(String nodePath, CreateMode createMode) {
        try {
            Stat stat = client.checkExists().forPath(nodePath);

            if (stat != null) {
                return stat.toString();
            } else {
                return client.create()
                        .creatingParentsIfNeeded()
//                        .withProtection()
                        .withMode(createMode)
                        .forPath(nodePath);
            }
        } catch (Exception e) {
            logger.error("create zookeeper node error :", e);
            return null;
        }
    }

    @Override
    public String createNode(String nodePath, CreateMode createMode, byte[] data) {
        try {
            Stat stat = client.checkExists().forPath(nodePath);

            if (stat != null) {
                return stat.toString();
            } else {
                return client.create()
                        .creatingParentsIfNeeded()
                        .withMode(createMode)
                        .forPath(nodePath, data);
            }
        } catch (Exception e) {
            logger.error("create zookeeper node error :", e);
            return null;
        }
    }

    @Override
    public CuratorFramework getClient() {
        return client;
    }

    @Override
    public void addChildrenCacheListener(String nodePath, PathChildrenCacheListener childrenCacheListener) {
        try {
            PathChildrenCache childrenCache = new PathChildrenCache(client, nodePath, true);

            childrenCache.getListenable().addListener(childrenCacheListener);
            childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);

            logger.info("addChildrenCacheListener success");
        } catch (Exception e) {
            logger.error("addChildrenCacheListener error :", e);
        }
    }

    @Override
    public void setData(String registerPath, byte[] data) {
        try {
            client.setData().forPath(registerPath, data);
            logger.info("setData success");
        } catch (Exception e) {
            logger.error("setData error :", e);
        }
    }
}
