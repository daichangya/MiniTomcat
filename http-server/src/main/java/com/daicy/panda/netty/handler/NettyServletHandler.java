package com.daicy.panda.netty.handler;

import com.daicy.panda.netty.PandaServerBuilder;
import com.daicy.panda.netty.TracingThreadPoolExecutor;
import com.daicy.panda.netty.security.SecurityHeadersFilter;
import com.daicy.panda.netty.servlet.impl.*;
import com.daicy.panda.netty.servlet.impl.filter.FilterChainFactory;
import com.daicy.panda.netty.servlet.impl.filter.FilterChainImpl;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.panda.netty.handler
 * @date:19-11-22
 */
@Slf4j
public class NettyServletHandler {


    public static void handleRequest(ServletRequest servletRequest, ServletResponse servletResponse) {
        ServletRequestImpl servletRequestImpl = (ServletRequestImpl) servletRequest;
        ServletResponseImpl servletResponseImpl = (ServletResponseImpl) servletResponse;
        
        // 获取线程池执行器和配置
        PandaServerBuilder builder = ServletContextImpl.get().getPandaServerBuilder();
        TracingThreadPoolExecutor asyncExecutor = builder != null ? builder.executor() : null;
        boolean useThreadPool = builder != null ? builder.isUseThreadPool() : false;
        
        if (!useThreadPool || asyncExecutor == null) {
            // 如果没有线程池，直接在IO线程执行（不推荐，但保持兼容性）
            try {
                handleRequest0(servletRequestImpl, servletResponseImpl);
            } catch (Throwable t) {
                ExceptionHandler.handleException(
                        servletRequestImpl.getChannelHandlerContext(),
                        servletRequestImpl.getOriginalRequest(),
                        servletResponseImpl,
                        t,
                        servletRequestImpl.isKeepAlive());
            }
        } else {
            // 使用线程池执行业务逻辑，避免阻塞Netty IO线程
            asyncExecutor.execute(() -> {
                try {
                    handleRequest0(servletRequestImpl, servletResponseImpl);
                } catch (Throwable t) {
                    ExceptionHandler.handleException(
                            servletRequestImpl.getChannelHandlerContext(),
                            servletRequestImpl.getOriginalRequest(),
                            servletResponseImpl,
                            t,
                            servletRequestImpl.isKeepAlive());
                }
            });
        }
    }

    public static void handleRequest0(ServletRequestImpl servletRequestImpl, ServletResponseImpl servletResponseImpl) 
            throws ServletException, IOException {
        // 添加安全响应头
        SecurityHeadersFilter.addSecurityHeaders(servletRequestImpl, servletResponseImpl);
        
        RequestDispatcherImpl dispatcher = (RequestDispatcherImpl) ServletContextImpl.get().getRequestDispatcher(servletRequestImpl.getRequestURI());
        if (dispatcher == null) {
            servletResponseImpl.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        dispatcher.dispatch(servletRequestImpl, servletResponseImpl);
    }
}
