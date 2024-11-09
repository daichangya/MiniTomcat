package com.daicy.minitomcat;

/**
 * @author 代长亚
 * 一个简单的 HTTP 服务器，用于处理 GET 请求并返回静态文件。
 */
public class HttpServer {
    static final String WEB_ROOT = "webroot"; // 静态文件根目录

    public static void main(String[] args) {
        HttpConnector connector = new HttpConnector();
        connector.start();
    }

}