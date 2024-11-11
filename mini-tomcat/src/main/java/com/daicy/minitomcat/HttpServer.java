package com.daicy.minitomcat;

import com.daicy.minitomcat.servlet.ServletContextImpl;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import java.util.Enumeration;

/**
 * @author 代长亚
 * 一个简单的 HTTP 服务器，用于处理 GET 请求并返回静态文件。
 */
public class HttpServer {
    static final String WEB_ROOT = "webroot"; // 静态文件根目录

    public static WebXmlServletContainer parser;

    public static void main(String[] args) {

        // 1. 创建 ServletContext 实例
        ServletContext servletContext = new ServletContextImpl();
        try {
            parser = new WebXmlServletContainer();
            parser.parse("/web.xml",servletContext);

            HttpConnector connector = new HttpConnector();
            connector.start();

        }finally {
            Enumeration<Servlet> servlets = servletContext.getServlets();
            if(servlets != null){
                while (servlets.hasMoreElements()){
                    Servlet servlet = servlets.nextElement();
                    servlet.destroy();
                }
            }
        }
    }

}