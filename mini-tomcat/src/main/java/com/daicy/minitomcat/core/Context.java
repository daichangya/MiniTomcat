package com.daicy.minitomcat.core;

import com.daicy.minitomcat.Lifecycle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface Context extends Lifecycle {
//    // 加载所有 Servlet
//    void load() throws ServletException;
//
//    // 卸载所有 Servlet
//    void unload();

    // 获取 Web 应用中的所有 Wrapper
    List<Wrapper> getWrappers();
}