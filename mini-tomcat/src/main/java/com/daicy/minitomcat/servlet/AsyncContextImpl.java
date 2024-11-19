package com.daicy.minitomcat.servlet;

import javax.servlet.*;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncContextImpl implements AsyncContext {

    private ServletRequest request;

    private ServletResponse response;

    private static ExecutorService executor = Executors.newCachedThreadPool();


    public AsyncContextImpl(ServletRequest request, ServletResponse response) {
        this.request = request;
        this.response = response;
    }

    @Override
    public void start(Runnable run) {
        executor.submit(run);
    }

    @Override
    public void complete() {
        try {
            // 完成异步响应
            HttpServletResponseImpl response = (HttpServletResponseImpl) this.response;
            response.sendResponse();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ServletRequest getRequest() {
        return null;
    }

    @Override
    public ServletResponse getResponse() {
        return null;
    }

    @Override
    public boolean hasOriginalRequestAndResponse() {
        return false;
    }

    @Override
    public void dispatch() {

    }

    @Override
    public void dispatch(String path) {

    }

    @Override
    public void dispatch(ServletContext context, String path) {

    }


    @Override
    public void addListener(AsyncListener listener) {

    }

    @Override
    public void addListener(AsyncListener listener, ServletRequest servletRequest, ServletResponse servletResponse) {

    }

    @Override
    public <T extends AsyncListener> T createListener(Class<T> clazz) throws ServletException {
        return null;
    }

    @Override
    public void setTimeout(long timeout) {

    }

    @Override
    public long getTimeout() {
        return 0;
    }
}
