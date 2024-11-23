package com.daicy.minitomcat;

import com.daicy.minitomcat.core.Valve;
import com.daicy.minitomcat.core.ValveContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LogValve implements Valve {

    @Override
    public void invoke(HttpServletRequest request, HttpServletResponse response, ValveContext context) {
        LogManager.getLogger().info("LogValve: Logging request " + request.getRequestURI());
        context.invokeNext(request, response); // 调用下一个 Valve
    }
}