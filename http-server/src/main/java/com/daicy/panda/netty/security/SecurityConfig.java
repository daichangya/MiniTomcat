package com.daicy.panda.netty.security;

import lombok.Getter;
import lombok.Setter;

/**
 * 安全配置类
 * 用于配置安全响应头、Cookie安全属性等
 *
 * @author daichangya
 */
@Getter
@Setter
public class SecurityConfig {

    /**
     * 是否启用安全响应头
     */
    private boolean enableSecurityHeaders = true;

    /**
     * X-Frame-Options 响应头值
     * 可选值: DENY, SAMEORIGIN, ALLOW-FROM
     */
    private String xFrameOptions = "SAMEORIGIN";

    /**
     * X-Content-Type-Options 响应头值
     * 通常设置为 "nosniff"
     */
    private String xContentTypeOptions = "nosniff";

    /**
     * X-XSS-Protection 响应头值
     * 可选值: 0, 1, 1; mode=block
     */
    private String xXssProtection = "1; mode=block";

    /**
     * Strict-Transport-Security (HSTS) 响应头值
     * 格式: max-age=31536000; includeSubDomains
     */
    private String strictTransportSecurity = null; // 默认不启用，仅在HTTPS时启用

    /**
     * Content-Security-Policy 响应头值
     */
    private String contentSecurityPolicy = null; // 默认不启用

    /**
     * Referrer-Policy 响应头值
     * 可选值: no-referrer, no-referrer-when-downgrade, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin, unsafe-url
     */
    private String referrerPolicy = "strict-origin-when-cross-origin";

    /**
     * Session Cookie 是否启用 HttpOnly
     */
    private boolean sessionCookieHttpOnly = true;

    /**
     * Session Cookie 是否启用 Secure（仅在HTTPS时）
     */
    private boolean sessionCookieSecure = false; // 根据请求是否HTTPS动态设置

    /**
     * Session Cookie SameSite 属性
     * 可选值: Strict, Lax, None
     */
    private String sessionCookieSameSite = "Lax";

    /**
     * Session Cookie名称
     */
    private String sessionCookieName = "JSESSIONID";

    /**
     * 是否启用CORS
     */
    private boolean enableCors = false;

    /**
     * CORS允许的源
     */
    private String corsAllowedOrigins = "*";

    /**
     * CORS允许的方法
     */
    private String corsAllowedMethods = "GET, POST, PUT, DELETE, OPTIONS";

    /**
     * CORS允许的请求头
     */
    private String corsAllowedHeaders = "*";

    /**
     * CORS是否允许凭证
     */
    private boolean corsAllowCredentials = false;

    /**
     * 单例实例
     */
    private static SecurityConfig instance = new SecurityConfig();

    private SecurityConfig() {
    }

    public static SecurityConfig getInstance() {
        return instance;
    }

    /**
     * 根据请求是否使用HTTPS设置Session Cookie Secure属性
     */
    public boolean shouldSessionCookieBeSecure(boolean isSecure) {
        return sessionCookieSecure || isSecure;
    }

    public String getSessionCookieName() {
        return sessionCookieName;
    }

    public void setSessionCookieName(String sessionCookieName) {
        this.sessionCookieName = sessionCookieName;
    }
}

