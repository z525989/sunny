# sunny

## 介绍
基于SpringBoot开发，使用netty开发的网络通讯框架
带websocket、rpc通讯功能

## 软件架构

###sunny-core

###sunny-websocket

###sunny-rpc

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
