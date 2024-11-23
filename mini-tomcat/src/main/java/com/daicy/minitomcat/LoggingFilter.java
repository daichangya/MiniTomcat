package com.daicy.minitomcat;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;


public class LoggingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 初始化操作，如果有需要可以在这里读取配置参数等
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        long startTime = System.currentTimeMillis();
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        LogManager.getLogger().info("Request started at: " + startTime + " for path: " + request.getRequestURI());

        filterChain.doFilter(servletRequest, servletResponse);

        long endTime = System.currentTimeMillis();
        LogManager.getLogger().info("Request completed at: " + endTime + " for path: " + request.getRequestURI() + " Took: " + (endTime - startTime) + "ms");
    }

    @Override
    public void destroy() {
        // 清理资源操作
    }
}