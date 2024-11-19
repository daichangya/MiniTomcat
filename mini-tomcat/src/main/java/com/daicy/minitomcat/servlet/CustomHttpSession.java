package com.daicy.minitomcat.servlet;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.util.*;

// 自定义类模拟实现HttpSession接口的部分功能
public class CustomHttpSession implements HttpSession {

    private String id;
    private Date creationTime;
    private Date lastAccessedTime;
    private int maxInactiveInterval;
    private Map<String, Object> attributes = new HashMap<>();

    public CustomHttpSession(String sessionId) {
        this.id = sessionId;
        this.creationTime = new Date();
        this.lastAccessedTime = new Date();
        this.maxInactiveInterval = 1800; // 设置默认的会话超时时间为30分钟（单位：秒）
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getCreationTime() {
        return creationTime.getTime();
    }

    @Override
    public long getLastAccessedTime() {
        return lastAccessedTime.getTime();
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        this.maxInactiveInterval = interval;
    }

    @Override
    public int getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    @Override
    public javax.servlet.http.HttpSessionContext getSessionContext() {
        // 在Servlet 3.1之后，HttpSessionContext接口已被废弃，这里返回null
        return null;
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public Object getValue(String name) {
        return null;
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return new Enumeration<String>() {
            private final Iterator<String> iterator = attributes.keySet().iterator();

            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public String nextElement() {
                return iterator.next();
            }
        };
    }

    @Override
    public String[] getValueNames() {
        return new String[0];
    }

    @Override
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    @Override
    public void putValue(String name, Object value) {

    }

    @Override
    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    @Override
    public void removeValue(String name) {

    }

    @Override
    public void invalidate() {
        attributes.clear();
    }

    @Override
    public boolean isNew() {
        // 简单判断，如果会话创建时间和最后访问时间相差在一定范围内，认为是新会话
        long timeDiff = getLastAccessedTime() - getCreationTime();
        return timeDiff < 1000; // 这里假设1秒内为新会话
    }

    public boolean isExpired() {
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastAccessedTime.getTime()) > (maxInactiveInterval * 1000L);
    }

    // 辅助方法，用于根据请求更新最后访问时间
    public void updateLastAccessedTime() {
        this.lastAccessedTime = new Date();
    }
}