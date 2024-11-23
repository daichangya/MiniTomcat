package com.daicy.minitomcat;


import com.daicy.minitomcat.core.StandardContext;
import com.daicy.minitomcat.core.Wrapper;
import com.daicy.minitomcat.servlet.Response;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ServletProcessor {

    private ResponseHeaderHandler headerHandler = new ResponseHeaderHandler();


    public void process(HttpServletRequest request, HttpServletResponse response) {
        String uri = request.getRequestURI();
        Response httpServletResponseImpl = (Response) response;
        try {
            StandardContext standardContext = HttpServer.context;
            Wrapper wrapper = standardContext.getWrapper(uri);
            try {
                RequestFacade requestFacade = new RequestFacade(request);
                ResponseFacade responseFacade = new ResponseFacade(response);

                headerHandler.applyHeaders(requestFacade, responseFacade, requestFacade.getSession().getId());
                List<Filter> filters = HttpServer.filterManager.getFilters();
                FilterChain filterChain = new FilterChain() {
                    int index = 0;
                    @Override
                    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
                        if (index == filters.size()) {
                            try {
                                wrapper.invoke((HttpServletRequest) request, (HttpServletResponse) response);
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
                filterChain.doFilter(requestFacade, responseFacade);
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
