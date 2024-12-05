package com.daicy.minitomcat.core;

import com.daicy.minitomcat.*;
import com.daicy.minitomcat.servlet.HttpServletResponseImpl;
import com.google.common.collect.Lists;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionListener;

import java.util.*;
import java.util.stream.Collectors;

public class StandardContext implements Context{

    private Map<String, Wrapper> wrapperMap = new HashMap<>();

    private WebXmlServletContainer config;


    private static ServletContextListenerManager servletContextListenerManager = new ServletContextListenerManager();

    public static HttpSessionListenerManager sessionListenerManager = new HttpSessionListenerManager();

    public static FilterManager filterManager = new FilterManager();


    public StandardContext(String configFilePath) throws ServletException {
        config = new WebXmlServletContainer();
        config.loadConfig(configFilePath);
        load();
    }

    public Wrapper getWrapper(String servletPath) {
        String servletName = config.getServletName(servletPath);
        return getWrapperByName(servletName);
    }

    public Wrapper getWrapperByName(String servletName) {
        return wrapperMap.get(servletName);
    }

    @Override
    public void load() throws ServletException {
        Map<String, ServletConfig> servletConfigMap = config.getServletConfigMap();
        for (String className : servletConfigMap.keySet()) {
            ServletConfig servletConfig = servletConfigMap.get(className);
            Wrapper wrapper = new StandardWrapper(servletConfig, className);
            wrapper.loadServlet();
            wrapperMap.put(servletConfig.getServletName(), wrapper);
        }
    }

    @Override
    public void unload() {
        List<Wrapper> wrappers = getWrappers();
        if(null == wrappers){
            return ;
        }
        wrappers.forEach(Wrapper::unloadServlet);
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

    public  void  addFilter(Filter filter){
        filterManager.addFilter(filter);
    }

    public  void  addListener(ServletContextListener listener){
        servletContextListenerManager.addListener(listener);
    }

    public void addSessionListener(HttpSessionListener listener){
    	sessionListenerManager.addListener(listener);
    }

    public void notifyContextInitialized(ServletContextEvent sce){
        servletContextListenerManager.notifyContextInitialized(sce);
    }

    public void notifyContextDestroyed(ServletContextEvent sce){
        servletContextListenerManager.notifyContextDestroyed(sce);
    }

}
