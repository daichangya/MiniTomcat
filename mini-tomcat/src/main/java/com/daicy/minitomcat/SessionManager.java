package com.daicy.minitomcat;

import com.daicy.minitomcat.servlet.CustomHttpSession;

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
        sessions.remove(sessionId);
    }
}