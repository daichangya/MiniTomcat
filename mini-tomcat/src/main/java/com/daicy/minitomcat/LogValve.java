package com.daicy.minitomcat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LogValve implements Valve {

    @Override
    public void invoke(HttpServletRequest request, HttpServletResponse response, ValveContext context) {
        System.out.println("LogValve: Logging request " + request.getRequestURI());
        context.invokeNext(request, response); // 调用下一个 Valve
    }
}