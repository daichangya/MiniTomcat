package com.daicy.minitomcat.core;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Wrapper {
    // 初始化 Wrapper，加载 Servlet
    void loadServlet() throws ServletException;

    // 调用 Servlet 的 service 方法处理请求
    void invoke(HttpServletRequest request, HttpServletResponse response) throws Exception;

    // 销毁 Servlet
    void unloadServlet();

    // 获取绑定的 Servlet 实例
    Servlet getServlet();
}