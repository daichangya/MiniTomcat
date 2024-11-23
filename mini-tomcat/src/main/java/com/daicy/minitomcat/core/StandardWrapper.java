package com.daicy.minitomcat.core;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StandardWrapper implements Wrapper {


    private Servlet servlet;

    private String className;

    private ServletConfig servletConfig;

    public StandardWrapper(ServletConfig servletConfig, String className) {
        this.servletConfig = servletConfig;
        this.className = className;
    }

    @Override
    public void start() throws ServletException {
        try {
            // 加载 Servlet 实例
            servlet = (Servlet) Class.forName(className).newInstance();
            servlet.init(servletConfig);
        } catch (Exception e) {
            throw new ServletException("Failed to load servlet", e);
        }
    }

    @Override
    public void invoke(HttpServletRequest request, HttpServletResponse response) throws Exception {
        servlet.service(request, response);
    }


    @Override
    public void stop() {
        if (servlet != null) {
            servlet.destroy();
        }
    }

    @Override
    public Servlet getServlet() {
        return servlet;
    }
}