package com.daicy.minitomcat;

import com.daicy.minitomcat.core.StandardContext;
import com.daicy.minitomcat.log.ConsoleLogger;
import com.daicy.minitomcat.log.FileLogger;
import com.daicy.minitomcat.log.LogLevel;
import com.daicy.minitomcat.log.LogManager;
import com.daicy.minitomcat.servlet.ServletContextImpl;

import javax.servlet.*;

/**
 * @author 代长亚
 * 一个简单的 HTTP 服务器，用于处理 GET 请求并返回静态文件。
 */
public class HttpServer {
    // 静态文件根目录
    static final String WEB_ROOT = "webroot";

    public static final String CONF_PATH = "/conf";

    public static final String WEB_XML = CONF_PATH+ "/web.xml";

    public static ServletContextImpl servletContext = new ServletContextImpl();

    public static StandardContext context;

    public static HotDeployment hotDeployment = new HotDeployment();

    public static FilterManager filterManager = new FilterManager();

    private static ServletContextListenerManager servletContextListenerManager = new ServletContextListenerManager();

    public static HttpSessionListenerManager sessionListenerManager = new HttpSessionListenerManager();

    public static void main(String[] args) throws Exception {
        Thread daemonThread = new Thread(() -> {
            hotDeployment.startDeploymentMonitor();
        });
        // 将线程设置为守护线程，必须在启动线程之前调用
        daemonThread.setDaemon(true);
        daemonThread.start();

        LogManager.addLogger(new ConsoleLogger(LogLevel.INFO));
        LogManager.addLogger(new FileLogger(LogLevel.DEBUG, "mini-tomcat.log"));


        servletContextListenerManager.addListener(new ServletContextListenerImpl());
        sessionListenerManager.addListener(new HttpSessionListenerImpl());
        // 启动监听器
        servletContextListenerManager.notifyContextInitialized(new ServletContextEvent(servletContext));
        context = new StandardContext(WEB_XML);
        context.start();
        filterManager.addFilter(new LoggingFilter());
        HttpConnector connector = new HttpConnector();
        connector.start();

        // 模拟服务器关闭
        Runtime.getRuntime().addShutdownHook(new Thread(HttpServer::stop));
    }

    public static void stop() {
        try {
            LogManager.info("Server stopping...");
            context.stop();
            servletContextListenerManager.notifyContextDestroyed(new ServletContextEvent(servletContext));
            SessionManager.removeSession();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}