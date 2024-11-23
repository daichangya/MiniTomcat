package com.daicy.minitomcat.core;

import com.daicy.minitomcat.WebXmlServletContainer;
import com.google.common.collect.Lists;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import java.util.*;
import java.util.stream.Collectors;

public class StandardContext implements Context{

    private Map<String, Wrapper> wrapperMap = new HashMap<>();

    private WebXmlServletContainer config;

    public StandardContext(String configFilePath) throws Exception {
        config = new WebXmlServletContainer();
        config.loadConfig(configFilePath);
        start();
    }

    public Wrapper getWrapper(String servletPath) {
        String servletName = config.getServletName(servletPath);
        return getWrapperByName(servletName);
    }

    public Wrapper getWrapperByName(String servletName) {
        return wrapperMap.get(servletName);
    }

    @Override
    public void start() throws Exception {
        Map<String, ServletConfig> servletConfigMap = config.getServletConfigMap();
        for (String className : servletConfigMap.keySet()) {
            ServletConfig servletConfig = servletConfigMap.get(className);
            Wrapper wrapper = new StandardWrapper(servletConfig, className);
            wrapper.start();
            wrapperMap.put(servletConfig.getServletName(), wrapper);
        }
    }

    @Override
    public void stop() throws Exception {
        List<Wrapper> wrappers = getWrappers();
        if(null == wrappers){
            return ;
        }
        for (Wrapper wrapper : wrappers){
            wrapper.stop();
        }
    }

    @Override
    public List<Wrapper> getWrappers() {
        return new ArrayList<>(wrapperMap.values());
    }

    public List<Servlet> getServlets() {
        List<Wrapper> wrappers = getWrappers();
        if(null == wrappers){
            return Lists.newArrayList();
        }
        return wrappers.stream().map(Wrapper::getServlet).collect(Collectors.toList());
    }


    public List<String> getServletNames() {
        return config.getServletNames();
    }
}
