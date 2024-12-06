package com.daicy.minitomcat;

import com.daicy.minitomcat.log.LogManager;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class HttpSessionListenerImpl implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        HttpSession session = httpSessionEvent.getSession();
        LogManager.info("Session created with ID: " + session.getId());
        // 可以在这里进行与新会话相关的初始化操作，如创建会话相关的缓存
        session.setAttribute("created", true);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        HttpSession session = httpSessionEvent.getSession();
        LogManager.info("Session destroyed with ID: " + session.getId());
        // 可以在这里清理与该会话相关的资源
        session.removeAttribute("created");
    }
}