package com.daicy.minitomcat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

public class ResponseHeaderHandler {

    // 设置Connection 头信息
    public void setConnectionBasedOnRequest(HttpServletRequest request, HttpServletResponse response) {
        String connection = request.getHeader("Connection");
        if (connection != null) {
            response.setHeader("Connection",connection);  // 将请求的内容类型应用到响应
        }
    }

    // 处理跨域头信息
    public void setCorsHeaders(HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        if (origin != null) {
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        }
    }

    // 设置内容类型
    public void setContentTypeBasedOnRequest(HttpServletRequest request, HttpServletResponse response) {
        String contentType = request.getHeader("Content-Type");
        if (contentType != null) {
            response.setContentType(contentType);  // 将请求的内容类型应用到响应
        }
    }

    // 处理内容编码（压缩）头信息
    public void setContentEncoding(HttpServletRequest request, HttpServletResponse response) {
        String acceptEncoding = request.getHeader("Accept-Encoding");
        if (acceptEncoding != null && acceptEncoding.contains("gzip")) {
            response.setHeader("Content-Encoding", "gzip");
            // 可以在这里对响应内容进行 gzip 压缩
        }
    }

    // 设置缓存控制头信息
    public void setCacheControl(HttpServletRequest request, HttpServletResponse response) {
        // 默认不缓存，可以根据需要定制
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        // 可根据 If-Modified-Since 等头信息处理缓存逻辑
        String ifModifiedSince = request.getHeader("If-Modified-Since");
        if (ifModifiedSince != null) {
            // 响应 304 Not Modified, 不发送响应主体
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        }
    }

    // 处理 Session ID（如果有）
    public void handleSessionCookie(HttpServletRequest request, HttpServletResponse response, String sessionId) {
        if (sessionId != null) {
            response.setHeader("Set-Cookie", "JSESSIONID=" + sessionId + "; Path=/; HttpOnly");
        }
    }

    // 将所有设置传递到 HttpServletResponse
    public void applyHeaders(HttpServletRequest request, HttpServletResponse response, String sessionId) {
        setConnectionBasedOnRequest(request, response);
        setCorsHeaders(request, response);           // 跨域处理
        setContentTypeBasedOnRequest(request, response);  // 设置内容类型
        setContentEncoding(request, response);       // 内容编码
        setCacheControl(request, response);          // 缓存控制
        handleSessionCookie(request, response, sessionId); // 设置 Session Cookie
    }
}
