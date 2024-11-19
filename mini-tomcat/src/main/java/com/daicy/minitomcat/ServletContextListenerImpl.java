package com.daicy.minitomcat;

import javax.servlet.*;

public class ServletContextListenerImpl implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        System.out.println("Servlet context initialized.");
        // 在这里可以进行初始化操作，如加载配置文件等
        servletContext.setAttribute("initialized", true);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        System.out.println("Servlet context destroyed.");
        // 在这里可以进行资源清理操作，如关闭数据库连接池等
        servletContext.removeAttribute("initialized");
    }
}
