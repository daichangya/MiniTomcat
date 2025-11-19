package com.daicy.panda.netty.monitoring;

import com.daicy.panda.netty.servlet.impl.ServletRequestImpl;
import com.daicy.panda.netty.servlet.impl.ServletResponseImpl;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 访问日志记录器
 * 记录HTTP请求和响应的详细信息
 *
 * @author daichangya
 */
@Slf4j
public class AccessLogger {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    private static final boolean ENABLE_ACCESS_LOG = Boolean.parseBoolean(
            System.getProperty("access.log.enabled", "true"));

    /**
     * 记录访问日志
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param duration 请求处理时长（毫秒）
     */
    public static void logAccess(ServletRequestImpl request, ServletResponseImpl response, long duration) {
        if (!ENABLE_ACCESS_LOG) {
            return;
        }

        try {
            String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
            String method = request.getMethod();
            String uri = request.getRequestURI();
            String protocol = request.getProtocol();
            int statusCode = response.getStatus();
            // 从响应头获取Content-Length
            String contentLengthHeader = response.getHeader("Content-Length");
            long contentLength = 0;
            if (contentLengthHeader != null) {
                try {
                    contentLength = Long.parseLong(contentLengthHeader);
                } catch (NumberFormatException e) {
                    // 忽略解析错误
                }
            }
            String remoteAddr = request.getRemoteAddr();
            String userAgent = request.getHeader("User-Agent");
            String referer = request.getHeader("Referer");

            // 格式化访问日志（类似Apache/Nginx日志格式）
            String logMessage = String.format(
                    "%s - - [%s] \"%s %s %s\" %d %d \"%s\" \"%s\" %dms",
                    remoteAddr,
                    timestamp,
                    method,
                    uri,
                    protocol,
                    statusCode,
                    contentLength,
                    referer != null ? referer : "-",
                    userAgent != null ? userAgent : "-",
                    duration
            );

            // 根据状态码选择日志级别
            if (statusCode >= 500) {
                log.error(logMessage);
            } else if (statusCode >= 400) {
                log.warn(logMessage);
            } else {
                log.info(logMessage);
            }
        } catch (Exception e) {
            log.error("Error logging access", e);
        }
    }

    /**
     * 记录慢请求
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param duration 请求处理时长（毫秒）
     */
    public static void logSlowRequest(ServletRequestImpl request, ServletResponseImpl response, long duration) {
        long slowRequestThreshold = Long.parseLong(
                System.getProperty("slow.request.threshold", "1000")); // 默认1秒

        if (duration > slowRequestThreshold) {
            log.warn("Slow request detected: {} {} took {}ms", 
                    request.getMethod(), request.getRequestURI(), duration);
        }
    }
}

