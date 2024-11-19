package com.daicy.minitomcat.servlet;

import com.daicy.minitomcat.SessionManager;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;

public class HttpServletRequestImpl  implements HttpServletRequest {
    private String method;
    private String requestURI;
    private String queryString;
    private Map<String, String> headers = new HashMap<>();
    private List<Cookie> cookies = new ArrayList<>();
    private HttpSession session;
    private String sessionId;
    private boolean sessionIdFromCookie;
    private boolean sessionIdChanged = false;
    private Map<String, String[]> parameters = new HashMap<>();

    private String characterEncoding = "UTF-8";

    private boolean asyncStarted = false;
    private AsyncContext asyncContext;

    public HttpServletRequestImpl(String method, String requestURI, String queryString, Map<String, String> headers) {
        this.method = method;
        this.requestURI = requestURI;
        this.queryString = queryString;
        this.headers = headers;

        // 解析 queryString 并填充参数映射
        if (queryString != null) {
            String[] pairs = queryString.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    parameters.put(keyValue[0], new String[]{keyValue[1]});
                }
            }
        }

        // 解析 cookies
        String cookieHeader = headers.get("Cookie");
        if (cookieHeader != null) {
            String[] cookiePairs = cookieHeader.split("; ");
            for (String cookiePair : cookiePairs) {
                String[] keyValue = cookiePair.split("=");
                if (keyValue.length == 2) {
                    Cookie cookie = new Cookie(keyValue[0], keyValue[1]);
                    cookies.add(cookie);
                    // 检查是否有 session ID
                    if ("JSESSIONID".equals(cookie.getName())) {
                        session = SessionManager.getOrCreateSession(cookie.getValue());
                    }
                }
            }
        }
        // 如果没有找到 JSESSIONID，则创建一个新的 session
        if (session == null) {
            session = SessionManager.createSession();
            cookies.add(new Cookie("JSESSIONID", session.getId()));
        }
    }

    @Override
    public boolean isAsyncStarted() {
        return asyncStarted;
    }

    @Override
    public AsyncContext getAsyncContext() {
        if (!asyncStarted) {
            throw new IllegalStateException("Async not started");
        }
        return asyncContext;
    }


    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        if (asyncStarted) {
            throw new IllegalStateException("Async already started");
        }
        asyncStarted = true;
        asyncContext = new AsyncContextImpl(this, servletResponse);
        return asyncContext;
    }

    @Override
    public boolean isAsyncSupported() {
        return asyncStarted;
    }


    @Override
    public HttpSession getSession() {
        return session;
    }

    @Override
    public HttpSession getSession(boolean create) {
        if (session == null && create) {
            session = SessionManager.createSession();
            cookies.add(new Cookie("JSESSIONID", session.getId()));
        }
        return session;
    }

    @Override
    public String getRequestedSessionId() {
        return this.sessionId;
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        if (sessionId == null) return false;
        HttpSession existingSession = SessionManager.getSession(sessionId);
        return existingSession != null && !((CustomHttpSession) existingSession).isExpired();
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return this.sessionIdFromCookie;
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return !this.sessionIdFromCookie;
    }

    @Override
    public String changeSessionId() {
        if (session == null) {
            getSession(true);
        }
        String newSessionId = UUID.randomUUID().toString();

        // 从存储中移除旧的 sessionId
        if (sessionId != null) {
            SessionManager.invalidateSession(sessionId);
        }

        // 更新新的 sessionId 并保存会话到存储
        sessionId = newSessionId;
        sessionIdChanged = true;
        return sessionId;
    }

    public boolean isSessionIdChanged() {
        return sessionIdChanged;
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public String getPathInfo() {
        return "";
    }

    @Override
    public String getPathTranslated() {
        return "";
    }

    @Override
    public String getContextPath() {
        return "";
    }

    @Override
    public String getRequestURI() {
        return requestURI;
    }

    @Override
    public StringBuffer getRequestURL() {
        return null;
    }

    @Override
    public String getServletPath() {
        return "";
    }

    @Override
    public String getQueryString() {
        return queryString;
    }

    @Override
    public String getRemoteUser() {
        return "";
    }

    @Override
    public boolean isUserInRole(String role) {
        return false;
    }

    @Override
    public String getAuthType() {
        return "";
    }

    @Override
    public Cookie[] getCookies() {
        return cookies.toArray(new Cookie[0]);
    }

    @Override
    public long getDateHeader(String name) {
        return 0;
    }

    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return null;
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(headers.keySet());
    }

    @Override
    public int getIntHeader(String name) {
        return 0;
    }

    @Override
    public Object getAttribute(String name) {
        return null;
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return null;
    }

    @Override
    public String getCharacterEncoding() {
        return characterEncoding;
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
        characterEncoding = env;
    }

    @Override
    public int getContentLength() {
        return 0;
    }

    @Override
    public long getContentLengthLong() {
        return 0;
    }

    @Override
    public String getContentType() {
        return "";
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return null;
    }

    @Override
    public String getParameter(String name) {
        String[] values = parameters.get(name);
        return values != null && values.length > 0 ? values[0] : null;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return null;
    }

    @Override
    public String[] getParameterValues(String name) {
        return new String[0];
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return parameters;
    }

    @Override
    public String getProtocol() { return "HTTP/1.1"; }
    @Override
    public String getScheme() { return "http"; }
    @Override
    public String getServerName() { return "localhost"; }
    @Override
    public int getServerPort() { return 80; }
    @Override
    public BufferedReader getReader() throws IOException {
        return null;
    }

    @Override
    public String getRemoteAddr() {
        return "";
    }

    @Override
    public String getRemoteHost() {
        return "";
    }

    @Override
    public void setAttribute(String name, Object o) {

    }

    @Override
    public void removeAttribute(String name) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return null;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return null;
    }

    @Override
    public String getRealPath(String path) {
        return "";
    }

    @Override
    public int getRemotePort() {
        return 0;
    }

    @Override
    public String getLocalName() {
        return "";
    }

    @Override
    public String getLocalAddr() {
        return "";
    }

    @Override
    public int getLocalPort() {
        return 0;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public AsyncContext startAsync() {
       return null;
    }


    @Override
    public DispatcherType getDispatcherType() {
        return null;
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }

    @Override
    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
        return false;
    }

    @Override
    public void login(String username, String password) throws ServletException {

    }

    @Override
    public void logout() throws ServletException {

    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        return Collections.emptyList();
    }

    @Override
    public Part getPart(String name) throws IOException, ServletException {
        return null;
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
        return null;
    }

    @Override
    public Principal getUserPrincipal() { return null; }

}