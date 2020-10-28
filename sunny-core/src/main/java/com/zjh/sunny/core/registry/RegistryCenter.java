package com.zjh.sunny.core.registry;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.zookeeper.CreateMode;

/**
 * 注册中心.
 *
 * @author zhangliang
 */
public interface RegistryCenter {

	/**
	 * 初始化注册中心.
	 */
	void init();

	/**
	 * 关闭注册中心.
	 */
	void close();

	/**
	 * 创建父节点
	 * @param managePath 父节点路径
	 */
	void createParentIfNeeded(String managePath);

	/**
	 * 创建节点
	 *
	 * @param nodePath 节点路径
	 *
	 * @param createMode  节点模式
	 * 		CreateMode.PERSISTENT: 创建节点后，不删除就永久存在
	 * 		CreateMode.PERSISTENT_SEQUENTIAL：节点path末尾会追加一个10位数的单调递增的序列
	 * 		CreateMode.EPHEMERAL：创建后，回话结束节点会自动删除
	 * 		CreateMode.EPHEMERAL_SEQUENTIAL：节点path末尾会追加一个10位数的单调递增的序列
	 *
	 * @return 节点路径
	 */
	String createNode(String nodePath, CreateMode createMode);

	/**
	 * 创建节点
	 *
	 * @param nodePath 节点路径
	 *
	 * @param createMode  节点模式
	 * 		CreateMode.PERSISTENT: 创建节点后，不删除就永久存在
	 * 		CreateMode.PERSISTENT_SEQUENTIAL：节点path末尾会追加一个10位数的单调递增的序列
	 * 		CreateMode.EPHEMERAL：创建后，回话结束节点会自动删除
	 * 		CreateMode.EPHEMERAL_SEQUENTIAL：节点path末尾会追加一个10位数的单调递增的序列
	 *
	 * @param data 保存数据
	 *
	 * @return 节点路径
	 */
	String createNode(String nodePath, CreateMode createMode, byte[] data);

	/**
	 * 获取zookeeper 客户端实例
	 */
	CuratorFramework getClient();

	/**
	 * 增加节点监听器
	 */
	void addChildrenCacheListener(String nodePath, PathChildrenCacheListener childrenCacheListener);

	/**
	 * 为节点设置数据
	 * @param registerPath 注册节点路径
	 * @param data 数据
	 */
	void setData(String registerPath, byte[] data);
}
