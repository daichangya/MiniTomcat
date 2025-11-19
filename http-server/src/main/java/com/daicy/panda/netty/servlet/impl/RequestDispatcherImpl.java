package com.daicy.panda.netty.servlet.impl;

import com.daicy.panda.netty.servlet.impl.filter.FilterChainFactory;
import com.daicy.panda.netty.servlet.impl.filter.FilterChainImpl;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.panda.netty.servlet.impl
 * @date:19-11-19
 */
public class RequestDispatcherImpl implements RequestDispatcher {

    /**
     * The servlet name for a named dispatcher.
     */
    private String name = null;

    /**
     * The servlet path for this RequestDispatcher.
     */
    private String servletPath = null;


    private HttpServlet httpServlet;


    public RequestDispatcherImpl(String servletName, String servletPath, HttpServlet servlet) {
        this.name = servletName;
        this.servletPath = servletPath;
        this.httpServlet = servlet;
    }


    @Override
    public void forward(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        ServletResponseImpl servletResponseImpl = (ServletResponseImpl) servletResponse;
        
        // 检查响应是否已提交
        if (servletResponseImpl.isCommitted()) {
            throw new IllegalStateException("Cannot forward. Response has already been committed");
        }
        
        if (httpServlet != null) {
            // 设置DispatcherType为FORWARD
            if (servletRequest instanceof ServletRequestImpl) {
                ((ServletRequestImpl) servletRequest).setDispatcherType(DispatcherType.FORWARD);
            }
            
            // 重置响应缓冲区
            servletResponseImpl.resetBuffer();
            
            // 执行forward：调用目标Servlet
            FilterChainImpl chain = FilterChainFactory.createFilterChain(
                    (ServletRequestImpl) servletRequest, httpServlet);
            if (chain != null) {
                chain.doFilter(servletRequest, servletResponse);
            } else {
                httpServlet.service(servletRequest, servletResponse);
            }
        } else {
            servletResponseImpl.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    public void include(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        if (httpServlet != null) {
            // 设置DispatcherType为INCLUDE
            if (servletRequest instanceof ServletRequestImpl) {
                ((ServletRequestImpl) servletRequest).setDispatcherType(DispatcherType.INCLUDE);
            }
            
            // include：包含目标Servlet的响应，不清空现有响应
            FilterChainImpl chain = FilterChainFactory.createFilterChain(
                    (ServletRequestImpl) servletRequest, httpServlet);
            if (chain != null) {
                chain.doFilter(servletRequest, servletResponse);
            } else {
                httpServlet.service(servletRequest, servletResponse);
            }
        } else {
            ((HttpServletResponse) servletResponse).sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    public void dispatch(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        ServletRequestImpl servletRequestImpl = (ServletRequestImpl) servletRequest;
        ServletResponseImpl servletResponseImpl = (ServletResponseImpl) servletResponse;
        FilterChainImpl chain = FilterChainFactory.createFilterChain(servletRequestImpl, httpServlet);
        if (chain != null) {
            // FilterChain会自动执行Filter链并在最后调用Servlet
            chain.doFilter(servletRequest, servletResponse);
        } else if (httpServlet != null) {
            // 如果没有Filter，直接调用Servlet
            httpServlet.service(servletRequest, servletResponse);
        } else {
            servletResponseImpl.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
