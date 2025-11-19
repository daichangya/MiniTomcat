package com.daicy.panda.netty.handler;

import com.daicy.panda.netty.servlet.impl.ServletResponseImpl;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 统一异常处理器
 * 处理Servlet处理过程中的异常，防止异常信息泄露，提供友好的错误响应
 *
 * @author daichangya
 */
@Slf4j
public class ExceptionHandler {

    private static final boolean DEV_MODE = Boolean.parseBoolean(
            System.getProperty("dev.mode", "false"));

    /**
     * 处理Servlet处理过程中的异常
     *
     * @param ctx              ChannelHandlerContext
     * @param request          请求对象
     * @param response         响应对象
     * @param throwable        异常
     * @param isKeepAlive      是否保持连接
     */
    public static void handleException(ChannelHandlerContext ctx,
                                       HttpRequest request,
                                       ServletResponseImpl response,
                                       Throwable throwable,
                                       boolean isKeepAlive) {
        try {
            int statusCode = determineStatusCode(throwable);
            String errorMessage = determineErrorMessage(throwable, statusCode);

            // 记录完整异常信息到日志
            log.error("Error processing request: {} {}", 
                    request.method(), request.uri(), throwable);

            // 设置响应状态
            response.setStatus(statusCode);

            // 设置错误响应体
            if (!response.isCommitted()) {
                String errorBody = buildErrorResponse(statusCode, errorMessage);
                response.setContentType("text/html; charset=UTF-8");
                response.setContentLength(errorBody.getBytes("UTF-8").length);
                
                try {
                    response.getWriter().write(errorBody);
                    response.getWriter().flush();
                } catch (IOException e) {
                    log.error("Failed to write error response", e);
                }
            }

            // 关闭响应
            response.close();
        } catch (Exception e) {
            log.error("Error in exception handler", e);
            // 如果异常处理器本身出错，直接关闭连接
            if (ctx != null && ctx.channel().isActive()) {
                ctx.close();
            }
        }
    }

    /**
     * 处理Channel异常
     *
     * @param ctx      ChannelHandlerContext
     * @param cause    异常
     */
    public static void handleChannelException(ChannelHandlerContext ctx, Throwable cause) {
        // 记录异常日志
        if (cause instanceof IOException) {
            log.debug("IO exception on channel: {}", ctx.channel().remoteAddress(), cause);
        } else {
            log.error("Unexpected exception on channel: {}", ctx.channel().remoteAddress(), cause);
        }

        // 关闭连接
        if (ctx.channel().isActive()) {
            ctx.close();
        }
    }

    /**
     * 根据异常类型确定HTTP状态码
     */
    private static int determineStatusCode(Throwable throwable) {
        if (throwable instanceof ServletException) {
            ServletException se = (ServletException) throwable;
            Throwable rootCause = se.getRootCause();
            if (rootCause != null) {
                return determineStatusCode(rootCause);
            }
            return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }
        
        if (throwable instanceof IOException) {
            return HttpServletResponse.SC_BAD_REQUEST;
        }
        
        if (throwable instanceof IllegalArgumentException) {
            return HttpServletResponse.SC_BAD_REQUEST;
        }
        
        if (throwable instanceof IllegalStateException) {
            return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }
        
        // 默认返回500
        return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    }

    /**
     * 确定错误消息
     * 生产环境不泄露详细异常信息
     */
    private static String determineErrorMessage(Throwable throwable, int statusCode) {
        if (DEV_MODE) {
            // 开发模式：返回详细异常信息
            StringBuilder sb = new StringBuilder();
            sb.append(throwable.getClass().getName());
            if (throwable.getMessage() != null) {
                sb.append(": ").append(throwable.getMessage());
            }
            return sb.toString();
        } else {
            // 生产模式：返回通用错误消息
            switch (statusCode) {
                case HttpServletResponse.SC_BAD_REQUEST:
                    return "Bad Request";
                case HttpServletResponse.SC_NOT_FOUND:
                    return "Not Found";
                case HttpServletResponse.SC_INTERNAL_SERVER_ERROR:
                default:
                    return "Internal Server Error";
            }
        }
    }

    /**
     * 构建错误响应HTML
     */
    private static String buildErrorResponse(int statusCode, String errorMessage) {
        String statusText = getStatusText(statusCode);
        return String.format(
                "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>%d %s</title>\n" +
                "    <style>\n" +
                "        body { font-family: Arial, sans-serif; text-align: center; padding: 50px; }\n" +
                "        h1 { color: #333; }\n" +
                "        p { color: #666; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <h1>%d %s</h1>\n" +
                "    <p>%s</p>\n" +
                "</body>\n" +
                "</html>",
                statusCode, statusText, statusCode, statusText, errorMessage);
    }

    /**
     * 获取HTTP状态文本
     */
    private static String getStatusText(int statusCode) {
        switch (statusCode) {
            case HttpServletResponse.SC_BAD_REQUEST:
                return "Bad Request";
            case HttpServletResponse.SC_UNAUTHORIZED:
                return "Unauthorized";
            case HttpServletResponse.SC_FORBIDDEN:
                return "Forbidden";
            case HttpServletResponse.SC_NOT_FOUND:
                return "Not Found";
            case HttpServletResponse.SC_INTERNAL_SERVER_ERROR:
                return "Internal Server Error";
            case HttpServletResponse.SC_SERVICE_UNAVAILABLE:
                return "Service Unavailable";
            default:
                return "Error";
        }
    }
}

