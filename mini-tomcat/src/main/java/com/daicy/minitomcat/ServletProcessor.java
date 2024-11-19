package com.daicy.minitomcat;


import com.daicy.minitomcat.servlet.FilterChainImpl;
import com.daicy.minitomcat.servlet.HttpServletResponseImpl;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static com.daicy.minitomcat.HttpProcessor.send404Response;

public class ServletProcessor {

    private ResponseHeaderHandler headerHandler = new ResponseHeaderHandler();


    public void process(HttpServletRequest request, HttpServletResponse response) {
        String uri = request.getRequestURI();
        HttpServletResponseImpl httpServletResponseImpl = (HttpServletResponseImpl) response;
        try {
            WebXmlServletContainer parser = HttpServer.parser;
            String servletName = parser.getServletName(uri);
            Servlet servlet = parser.getServlet(servletName);
            if (null == servlet){
                ServletConfig servletConfig = parser.getServletConfig(uri);
                servlet = ServletLoader.loadServlet(servletConfig);
                if (null == servlet){
                    return;
                }
                // 将初始化后的 Servlet 存储在 WebXmlServletContainer 中，后续可通过 WebXmlServletContainer 访问
                parser.setServlet(servletName, servlet);
            }
            try {
                headerHandler.applyHeaders(request, response, request.getSession().getId());
                FilterChainImpl chain = new FilterChainImpl(HttpServer.filterManager.getFilters());
                chain.doFilter(request, response);
                servlet.service(request, response);
            } catch (Exception e) {
                // 捕获异常并设置错误状态码
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Internal Server Error: " + e.getMessage());
            }
            if(!request.isAsyncStarted()){
                // 3. 发送响应
                httpServletResponseImpl.sendResponse();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
