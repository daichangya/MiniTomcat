package com.daicy.minitomcat;

import com.daicy.minitomcat.core.StandardContext;
import com.daicy.minitomcat.servlet.CustomHttpSession;
import com.daicy.minitomcat.servlet.ServletContextImpl;

import javax.servlet.*;
import javax.servlet.http.HttpSessionEvent;
import java.util.Enumeration;

/**
 * @author 代长亚
 * 一个简单的 HTTP 服务器，用于处理 GET 请求并返回静态文件。
 */
public class HttpServer {
    static final String WEB_ROOT = "webroot"; // 静态文件根目录

    public static ServletContextImpl servletContext = new ServletContextImpl();

    public static StandardContext context;

    public static void main(String[] args) throws ServletException {
        context = new StandardContext("/web.xml");
        context.addFilter(new LoggingFilter());
        context.addListener(new ServletContextListenerImpl());
        context.addSessionListener(new HttpSessionListenerImpl());
        // 启动监听器
        context.notifyContextInitialized(new ServletContextEvent(servletContext));
        HttpConnector connector = new HttpConnector();
        connector.start();

        // 模拟服务器关闭
        Runtime.getRuntime().addShutdownHook(new Thread(HttpServer::stop));
    }

    public static void stop() {
        System.out.println("Server stopping...");
        context.unload();
        context.notifyContextDestroyed(new ServletContextEvent(servletContext));
        SessionManager.removeSession();
    }

}