package com.daicy.minitomcat;

import com.daicy.minitomcat.core.StandardContext;
import com.daicy.minitomcat.servlet.CustomHttpSession;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {
    private static final Map<String, CustomHttpSession> sessions = new HashMap<>();

    public static CustomHttpSession getSession(String sessionId) {
        CustomHttpSession session = sessions.get(sessionId);
        if (session != null) {
            session.updateLastAccessedTime();
        }
        return session;
    }

    public static CustomHttpSession createSession() {
        String sessionId = UUID.randomUUID().toString();
        CustomHttpSession session = new CustomHttpSession(sessionId);
        sessions.put(sessionId, session);
        HttpSessionEvent sessionEvent = new HttpSessionEvent(session);
        if (session.isNew()) {
            StandardContext.sessionListenerManager.sessionCreated(sessionEvent);
        }
        return session;
    }

    public static CustomHttpSession getOrCreateSession(String sessionId) {
        CustomHttpSession session = sessions.get(sessionId);
        if (session == null) {
            session = createSession();
        }
        session.updateLastAccessedTime();
        return session;
    }

    public static void invalidateSession(String sessionId) {
        HttpSession session = sessions.get(sessionId);
        sessions.remove(sessionId);
        HttpSessionEvent sessionEvent = new HttpSessionEvent(session);
        StandardContext.sessionListenerManager.sessionDestroyed(sessionEvent);
    }

    public static void removeSession() {
        if (sessions != null) {
            for (CustomHttpSession session : sessions.values()){
                HttpSessionEvent sessionEvent = new HttpSessionEvent(session);
                StandardContext.sessionListenerManager.sessionDestroyed(sessionEvent);
            }
        }
    }
}