package com.daicy.panda.netty.security;

import com.daicy.panda.netty.servlet.impl.ServletRequestImpl;
import com.daicy.panda.netty.servlet.impl.ServletResponseImpl;
import lombok.extern.slf4j.Slf4j;

/**
 * 安全响应头过滤器
 * 自动为响应添加安全相关的HTTP响应头
 *
 * @author daichangya
 */
@Slf4j
public class SecurityHeadersFilter {

    private static final SecurityConfig securityConfig = SecurityConfig.getInstance();

    /**
     * 为响应添加安全响应头
     *
     * @param request  请求对象
     * @param response 响应对象
     */
    public static void addSecurityHeaders(ServletRequestImpl request, ServletResponseImpl response) {
        if (!securityConfig.isEnableSecurityHeaders()) {
            return;
        }

        // 如果响应已提交，不再添加响应头
        if (response.isCommitted()) {
            return;
        }

        boolean isSecure = request.isSecure();

        // X-Frame-Options: 防止点击劫持
        if (securityConfig.getXFrameOptions() != null) {
            response.setHeader("X-Frame-Options", securityConfig.getXFrameOptions());
        }

        // X-Content-Type-Options: 防止MIME类型嗅探
        if (securityConfig.getXContentTypeOptions() != null) {
            response.setHeader("X-Content-Type-Options", securityConfig.getXContentTypeOptions());
        }

        // X-XSS-Protection: XSS保护
        if (securityConfig.getXXssProtection() != null) {
            response.setHeader("X-XSS-Protection", securityConfig.getXXssProtection());
        }

        // Strict-Transport-Security: HSTS（仅在HTTPS时）
        if (isSecure && securityConfig.getStrictTransportSecurity() != null) {
            response.setHeader("Strict-Transport-Security", securityConfig.getStrictTransportSecurity());
        }

        // Content-Security-Policy: CSP
        if (securityConfig.getContentSecurityPolicy() != null) {
            response.setHeader("Content-Security-Policy", securityConfig.getContentSecurityPolicy());
        }

        // Referrer-Policy: 引用者策略
        if (securityConfig.getReferrerPolicy() != null) {
            response.setHeader("Referrer-Policy", securityConfig.getReferrerPolicy());
        }

        // CORS响应头
        if (securityConfig.isEnableCors()) {
            addCorsHeaders(response);
        }
    }

    /**
     * 添加CORS响应头
     */
    private static void addCorsHeaders(ServletResponseImpl response) {
        String allowedOrigins = securityConfig.getCorsAllowedOrigins();
        if (allowedOrigins != null && !allowedOrigins.isEmpty()) {
            response.setHeader("Access-Control-Allow-Origin", allowedOrigins);
        }

        if (securityConfig.isCorsAllowCredentials()) {
            response.setHeader("Access-Control-Allow-Credentials", "true");
        }

        String allowedMethods = securityConfig.getCorsAllowedMethods();
        if (allowedMethods != null && !allowedMethods.isEmpty()) {
            response.setHeader("Access-Control-Allow-Methods", allowedMethods);
        }

        String allowedHeaders = securityConfig.getCorsAllowedHeaders();
        if (allowedHeaders != null && !allowedHeaders.isEmpty()) {
            response.setHeader("Access-Control-Allow-Headers", allowedHeaders);
        }
    }
}

