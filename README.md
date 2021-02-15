# sunny

## 介绍
基于SpringBoot开发，使用netty开发的网络通讯框架<br>
带websocket、rpc通讯功能<br>
本框架支持分布式高可用部署，服务之间使用zookeeper作为注册中心<br>


## 软件架构

### sunny-core
公共代码模块，zookeeper redis相关API<br>
不需要引用，下边模块会自动导入

### sunny-websocket
1.	基于SpringBoot、netty、redis、zookeeper开发的长连接框架<br>
2.	简化websocket使用时候集成流程<br>
3.	使用zookeeper作为注册中心，支持分布式部署，新增节点自动通知到已经上线节点<br>
4.	长连接会话管理，会话信息储存于redis中，支持跨服务转发消息<br>
5.	服务之间通讯采用TCP，与websocket采用相同端口。使用netty动态解码器解决不同协议数据包解析问题<br>
6.	使用AOP编程，长连接协议通过注解形式定义，启动时候会自动扫描注解，绑定协议号<br>
7.	集成token校验功能，支持跨服务共享token


### sunny-rpc
rpc通讯模块，待开发

## 安装教程
mvn clean install

## 使用说明
1.  安装sunny到本地maven库
2.  在SpringBoot pom文件中，引入需要的模块
3.  在SpringBoot 启动器将 "com.zjh.sunny" 包名加入自动扫描
4.  在spring 配置文件中，填写相关配置：<br>
    redis配置： spring.redis<br>
    websocket配置：sunny.websocket<br>
    zookeeper配置：sunny.zookeeper<br>
5.  启动SpringBootApplication

## 参与贡献
