package com.daicy.minitomcat;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.ArrayList;
import java.util.List;

public class HttpSessionListenerManager {
    private List<HttpSessionListener> listeners = new ArrayList<>();

    public void addListener(HttpSessionListener listener) {
        listeners.add(listener);
    }

    public void sessionCreated(HttpSessionEvent sce) {
        for (HttpSessionListener listener : listeners) {
            listener.sessionCreated(sce);
        }
    }

    public void sessionDestroyed(HttpSessionEvent sce) {
        for (HttpSessionListener listener : listeners) {
            listener.sessionDestroyed(sce);
        }
    }
}
