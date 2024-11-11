package com.daicy.minitomcat.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class ServletConfigImpl implements ServletConfig {

    private String servletName;
    private ServletContext servletContext;
    private Map<String, String> initParameters;

    public ServletConfigImpl(String servletName, ServletContext servletContext, Map<String, String> initParameters) {
        this.servletName = servletName;
        this.servletContext = servletContext;
        this.initParameters = initParameters != null ? initParameters : new HashMap<>();
    }

    @Override
    public String getServletName() {
        return servletName;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public String getInitParameter(String name) {
        return initParameters.get(name);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        // 返回初始化参数的名称集合
        return Collections.enumeration(initParameters.keySet());
    }
}