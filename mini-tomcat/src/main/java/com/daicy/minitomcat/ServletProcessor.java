package com.daicy.minitomcat;


import com.daicy.minitomcat.servlet.FilterChainImpl;
import com.daicy.minitomcat.servlet.HttpServletResponseImpl;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

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
                Servlet finalServlet = servlet;
                List<Filter> filters = HttpServer.filterManager.getFilters();
                FilterChain filterChain = new FilterChain() {
                    int index = 0;
                    @Override
                    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
                        if (index == filters.size()) {
                            try {
                                finalServlet.service(request, response);
                            } catch (Exception e) {
                                HttpServletResponse httpServletResponse = (HttpServletResponse) response;
                                // 捕获异常并设置错误状态码
                                httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                                httpServletResponse.getWriter().write("Internal Server Error: " + e.getMessage());
                            }
                        } else {
                            Filter filter = filters.get(index);
                            index++;
                            filter.doFilter(request, response, this);
                        }
                    }
                };
                filterChain.doFilter(request, response);
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
