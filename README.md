## 微信公众号

扫码关注微信公众号，Java码界探秘。
![Java码界探秘](http://images.zthinker.com/qrcode_for_gh_1e2587cc42b1_258_1587996055777.jpg)

[https://zthinker.com/](https://zthinker.com/)

# MiniTomcat (https://github.com/daichangya/miniTomcat)

新开发了MiniTomcat系列课程

项目目录 https://github.com/daichangya/MiniTomcat/tree/chapter1/mini-tomcat

打造属于你的 MiniTomcat：深入理解 Web 容器核心架构与实现之路
https://zthinker.com/archives/minitomcat-start

MiniTomcat 项目大纲
https://zthinker.com/archives/minitomcat-xiang-mu-da-gang

第一章：实现基础 HTTP 服务器-MiniTomcat
https://zthinker.com/archives/di-1bu-shi-xian-ji-chu-http-fu-wu-qi

第二章：解析 HTTP 请求，支持静态文件-MiniTomcat
https://zthinker.com/archives/di-er-zhang-jie-xi-http-qing-qiu-zhi-chi-jing-tai-wen-jian

第三章：实现连接器（Connector）组件-MiniTomcat
https://zthinker.com/archives/di-san-bu-shi-xian-lian-jie-qi-connector-zu-jian-minitomcat

第四章：实现 Servlet 容器的基本功能-MiniTomcat系列
https://zthinker.com/archives/di-si-zhang-shi-xian-servlet-rong-qi-de-ji-ben-gong-neng-minitomcatxi-lie

第五章：支持 Servlet 配置和 URL 映射-MiniTomcat
https://zthinker.com/archives/di-wu-zhang-zhi-chi-servlet-pei-zhi-he-url-ying-she-minitomcat

第六章：支持 Session 和 Cookie-Minitomcat
https://zthinker.com/archives/di-liu-zhang-zhi-chi-session-he-cookie-minitomcat

第七章：实现多线程支持-Minitomcat
https://zthinker.com/archives/di-qi-zhang-shi-xian-duo-xian-cheng-zhi-chi-minitomcat


第八章：实现异步请求支持（基于 Servlet 协议）-Minitomcat
https://zthinker.com/archives/di-ba-zhang-shi-xian-yi-bu-qing-qiu-zhi-chi-ji-yu-servlet-xie-yi--minitomcat

第九章：实现过滤器（Filter）和监听器（Listener）-Minitomcat
https://zthinker.com/archives/di-jiu-zhang-shi-xian-guo-lu-qi-filter-he-jian-ting-qi-listener--minitomcat

第10章：实现 Valve 和 Pipeline 机制-MiniTomcat
https://zthinker.com/archives/di-10-zhang-shi-xian-valve-he-pipeline-ji-zhi-minitomcat


第 11 章：实现 Wrapper 和 Context-MiniTomcat
https://zthinker.com/archives/di-11-zhang-shi-xian-wrapper-he-context-minitomcat

第 12 章：实现 Facade（外观模式）-MiniTomcat
https://zthinker.com/archives/di-12-zhang-shi-xian-facade-wai-guan-mo-shi--minitomcat

第 13 章：实现生命周期管理（Lifecycle）-MiniTomcat
https://zthinker.com/archives/di-13-zhang-shi-xian-sheng-ming-zhou-qi-guan-li-lifecycle


第 14 章：实现 Log 模块-MiniTomcat
https://zthinker.com/archives/di-14-zhang-shi-xian-log-mo-kuai-minitomcat

第 15 章：支持配置热加载和自动部署-MiniTomcat
https://zthinker.com/archives/di-15-zhang-zhi-chi-pei-zhi-re-jia-zai-he-zi-dong-bu-shu-minitomcat


**这个项目是一个基于Netty的Java Web服务器，它提供了从简单HTTP服务器到集成Spring Boot等多个版本的演进。以下是对该项目的详细概述：**

1.  **项目名称与简介**：
    *   项目名称：panda
    *   简介：一个基于Netty的Java Web服务器，随着版本的迭代，逐步增加了更多功能，如静态文件下载、controller支持、servlet支持以及最终集成Spring Boot等。
2.  **技术栈**：
    *   主要技术：Netty, Spring Boot, Spring Web
    *   Netty：一个高性能、异步事件驱动的NIO框架，用于快速开发可维护的高性能协议服务器和客户端。
    *   Spring Boot：简化了基于Spring的应用开发，通过自动配置和启动类让开发者快速搭建Spring应用。
    *   Spring Web：Spring框架的Web模块，提供了全面的Web开发支持。
3.  **功能与特性**：
    *   支持静态文件下载。
    *   添加了controller和servlet支持，便于开发Web应用。
    *   集成Spring Boot，使得项目可以更容易地部署和运行。
    *   提供了异步处理逻辑和HTTP流事件分块传输的支持，提高了应用的性能和响应能力。

```
外部客户端 --(HTTP请求)--> Netty网络层  
                                |  
                                V  
                  Spring Boot应用层  
                    /             \  
                Controller/    Session管理  
                Servlet         (包括异步处理)  
                    \             /  
                    业务逻辑处理  
                        |  
                        V  
```

# panda 一个 基于Netty的Java Web服务器
0.1
* http协议

1.0
* 一个简单的Http Server

2.0 
* 通讯模型改为netty
* 支持静态文件下载

3.0
* 添加controller支持

4.0
* 添加servlet支持
* 对接spring web

5.0
* 集成spring boot,替换内嵌tomcat
* Netty spring boot Spring Web

6.0.0
* 新建模块 spring-boot-starter-netty

6.0.1
* 添加 channelHandle 线程池

6.0.2
* add session处理逻辑

6.0.3
* add 异步处理逻辑

6.0.4
* add http stream event chunked
