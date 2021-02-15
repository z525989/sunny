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
websocket框架<br>
数据包序列化采用fastjson<br>
websocket session储存与redis中<br>
链接到 服务A 的用户 支持转发消息 到 链接自服务B的用户

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
