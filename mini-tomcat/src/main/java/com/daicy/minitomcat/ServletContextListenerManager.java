package com.daicy.minitomcat;

import javax.servlet.*;
import java.util.List;
import java.util.ArrayList;

public class ServletContextListenerManager {
    private List<ServletContextListener> listeners = new ArrayList<>();

    public void addListener(ServletContextListener listener) {
        listeners.add(listener);
    }

    public void notifyContextInitialized(ServletContextEvent sce) {
        for (ServletContextListener listener : listeners) {
            listener.contextInitialized(sce);
        }
    }

    public void notifyContextDestroyed(ServletContextEvent sce) {
        for (ServletContextListener listener : listeners) {
            listener.contextDestroyed(sce);
        }
    }
}
