package com.daicy.panda.netty.servlet.impl;

import com.daicy.panda.netty.security.SecurityConfig;
import com.daicy.panda.netty.servlet.SessionThreadLocal;
import com.daicy.panda.netty.servlet.impl.ServletContextImpl;
import com.daicy.panda.netty.servlet.impl.SessionImpl;
import com.google.common.collect.Lists;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Locale;


/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.panda.http
 * @date:19-11-8
 */
@Slf4j
public class ServletResponseImpl implements HttpServletResponse {

    private final DefaultHttpResponse originalResponse;

    private ServletOutputStreamImpl servletOutputStream;

    private ChannelHandlerContext ctx;

    private PrintWriter printWriter;

    private boolean responseCommited = false;

    private boolean isSecure = false;

    public ServletResponseImpl(ChannelHandlerContext ctx, DefaultHttpResponse originalResponse) {
        this.ctx = ctx;
        this.originalResponse = originalResponse;
        this.servletOutputStream = new ServletOutputStreamImpl(this);
        this.printWriter = new PrintWriter(servletOutputStream);
    }

    @Override
    public void addCookie(Cookie cookie) {
        // 创建Netty Cookie对象
        DefaultCookie nettyCookie = new DefaultCookie(cookie.getName(), cookie.getValue());
        
        // 设置基本属性
        if (cookie.getDomain() != null) {
            nettyCookie.setDomain(cookie.getDomain());
        }
        if (cookie.getPath() != null) {
            nettyCookie.setPath(cookie.getPath());
        }
        if (cookie.getMaxAge() >= 0) {
            nettyCookie.setMaxAge(cookie.getMaxAge());
        }
        nettyCookie.setSecure(cookie.getSecure());
        
        // 应用安全配置
        SecurityConfig securityConfig = SecurityConfig.getInstance();
        
        // HttpOnly属性（如果Cookie未设置，使用安全配置）
        if (cookie.getComment() == null || !cookie.getComment().contains("HttpOnly")) {
            // Netty的DefaultCookie没有直接的HttpOnly设置方法
            // 需要在编码时处理
        }
        
        // 编码Cookie
        String cookieString = ServerCookieEncoder.STRICT.encode(nettyCookie);
        
        // 手动添加HttpOnly属性（如果启用）
        // 对于Session Cookie或配置为HttpOnly的Cookie
        boolean isSessionCookie = cookie.getName().equals(securityConfig.getSessionCookieName());
        if (securityConfig.isSessionCookieHttpOnly() && isSessionCookie) {
            if (!cookieString.contains("HttpOnly")) {
                cookieString += "; HttpOnly";
            }
        }
        
        // 添加SameSite属性（如果配置，仅对Session Cookie）
        if (isSessionCookie && securityConfig.getSessionCookieSameSite() != null) {
            if (!cookieString.contains("SameSite")) {
                cookieString += "; SameSite=" + securityConfig.getSessionCookieSameSite();
            }
        }
        
        // 根据请求是否HTTPS设置Secure属性（仅对Session Cookie）
        if (isSessionCookie) {
            // 使用响应中存储的isSecure标志
            if (securityConfig.shouldSessionCookieBeSecure(this.isSecure || cookie.getSecure())) {
                if (!cookieString.contains("Secure")) {
                    cookieString += "; Secure";
                }
            }
        }
        
        this.originalResponse.headers().add(HttpHeaderNames.SET_COOKIE, cookieString);
    }

    @Override
    public boolean containsHeader(String name) {
        return this.originalResponse.headers().contains(name);
    }

    @Override
    public String encodeURL(String url) {
        try {
            String characterEncoding = getCharacterEncoding();
            if (StringUtils.isEmpty(characterEncoding)) {
                return URLEncoder.encode(url);
            }
            return URLEncoder.encode(url, getCharacterEncoding());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error encoding url!", e);
        }
    }

    @Override
    public String encodeRedirectURL(String url) {
        return encodeURL(url);
    }

    @Override
    public String encodeUrl(String url) {
        return encodeURL(url);
    }

    @Override
    public String encodeRedirectUrl(String url) {
        return encodeURL(url);
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        this.originalResponse.setStatus(new HttpResponseStatus(sc, msg));
    }

    @Override
    public void sendError(int sc) throws IOException {
        this.originalResponse.setStatus(HttpResponseStatus.valueOf(sc));
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        setStatus(SC_FOUND);
        setHeader(HttpHeaderNames.LOCATION.toString(), location);
    }

    @Override
    public void setDateHeader(String name, long date) {
        this.originalResponse.headers().set(name, date);
    }

    @Override
    public void addDateHeader(String name, long date) {
        this.originalResponse.headers().add(name, date);
    }

    @Override
    public void setHeader(String name, String value) {
        this.originalResponse.headers().set(name, value);
    }

    @Override
    public void addHeader(String name, String value) {
        this.originalResponse.headers().add(name, value);
    }

    @Override
    public void setIntHeader(String name, int value) {
        this.originalResponse.headers().setInt(name, value);
    }

    @Override
    public void addIntHeader(String name, int value) {
        this.originalResponse.headers().addInt(name, value);
    }

    @Override
    public int getStatus() {
        return this.originalResponse.getStatus().code();
    }


    @Override
    public void setStatus(int sc) {
        this.originalResponse.setStatus(HttpResponseStatus.valueOf(sc));
    }

    @Override
    public void setStatus(int sc, String sm) {
        this.originalResponse.setStatus(new HttpResponseStatus(sc, sm));
    }


    @Override
    public String getHeader(String name) {
        return this.originalResponse.headers().get(name);
    }

    @Override
    public Collection<String> getHeaders(String name) {
        String value = this.getHeader(name);
        return null == value ? null : Lists.newArrayList(value);
    }

    @Override
    public Collection<String> getHeaderNames() {
        return this.originalResponse.headers().names();
    }

    @Override
    public String getCharacterEncoding() {
        return this.originalResponse.headers().get(HttpHeaderNames.CONTENT_ENCODING);
    }

    @Override
    public void setCharacterEncoding(String charset) {
        this.originalResponse.headers().set(HttpHeaderNames.CONTENT_ENCODING, charset);
    }


    @Override
    public String getContentType() {
        return this.originalResponse.headers().get(HttpHeaderNames.CONTENT_TYPE);
    }

    @Override
    public void setContentType(String type) {
        this.originalResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, type);
    }

    @Override
    public void setContentLength(int len) {
        HttpUtil.setContentLength(this.originalResponse, len);
    }

    @Override
    public ServletOutputStreamImpl getOutputStream() throws IOException {
        return servletOutputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return printWriter;
    }

    @Override
    public void setContentLengthLong(long len) {
        HttpUtil.setContentLength(this.originalResponse, len);
    }

    @Override
    public void setBufferSize(int size) {
    }

    private boolean flush = false;

    @Override
    public void flushBuffer() {
        this.servletOutputStream.flush();
//        boolean isKeepAlive = HttpUtil.isKeepAlive(originalResponse);
//        if (isKeepAlive) {
//            setContentLength(originalResponse.content().readableBytes());
//        }
    }

    @Override
    public int getBufferSize() {
        return this.servletOutputStream.getBufferSize();
    }

    @Override
    public void resetBuffer() {
        if (isCommitted())
            throw new IllegalStateException("Response already commited!");

        this.servletOutputStream.resetBuffer();
    }

    @Override
    public boolean isCommitted() {
        return this.responseCommited;
    }

    @Override
    public void reset() {
        if (isCommitted())
            throw new IllegalStateException("Response already commited!");

        this.originalResponse.headers().clear();
        this.resetBuffer();
    }

    @Override
    public void setLocale(Locale loc) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }


    public void close() {
        // 在响应提交前，自动添加Session Cookie（如果Session是新创建的）
        addSessionCookieIfNeeded();
        
        this.responseCommited = true;
        servletOutputStream.close();
        boolean isKeepAlive = HttpUtil.isKeepAlive(originalResponse);
        if(ctx.channel().isActive()){
            ChannelFuture channelFuture = ctx.write(DefaultLastHttpContent.EMPTY_LAST_CONTENT);
            if (!isKeepAlive && channelFuture != null) {
                channelFuture.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    /**
     * 如果需要，自动添加Session Cookie
     * 当Session是新创建时，自动添加Session Cookie到响应中
     */
    private void addSessionCookieIfNeeded() {
        SessionImpl session = SessionThreadLocal.sessionThreadLocal.get();
        if (session != null && session.isNew()) {
            // Session是新创建的，需要添加Cookie
            SecurityConfig securityConfig = SecurityConfig.getInstance();
            String sessionCookieName = ServletContextImpl.get().getPandaServerBuilder().getSssionCookieName();
            
            javax.servlet.http.Cookie sessionCookie = new javax.servlet.http.Cookie(
                    sessionCookieName, session.getId());
            sessionCookie.setPath("/");
            sessionCookie.setMaxAge(-1); // 会话Cookie
            
            // 根据请求是否HTTPS设置Secure
            // 这里需要从请求中获取，暂时通过检查响应头判断
            // 更好的方式是在创建ServletResponse时传入isSecure标志
            sessionCookie.setSecure(false); // 将在addCookie中根据配置调整
            
            addCookie(sessionCookie);
        }
    }

    public DefaultHttpResponse getOriginalResponse() {
        return originalResponse;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    /**
     * 设置请求是否使用HTTPS
     */
    public void setSecure(boolean secure) {
        this.isSecure = secure;
    }

    public boolean isSecure() {
        return isSecure;
    }
}
