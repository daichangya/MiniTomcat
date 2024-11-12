package com.daicy.minitomcat.servlet;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * @author changyadai
 */
public class HttpServletResponseImpl implements HttpServletResponse {
    private String characterEncoding = "UTF-8";

    private int statusCode = HttpServletResponse.SC_OK;
    private String statusMessage = "OK";
    private Map<String, List<String>> headers = new HashMap<>(); // 使用List支持多值头信息
    private List<Cookie> cookies = new ArrayList<>();
    private ByteArrayOutputStream body = new ByteArrayOutputStream();  // 存储响应主体内容
    private PrintWriter writer = new PrintWriter(new OutputStreamWriter(body,characterEncoding));  // 用于写入响应主体内容
    private OutputStream outputStream;

    public HttpServletResponseImpl(OutputStream outputStream) throws UnsupportedEncodingException {
        this.outputStream = outputStream;
    }

    @Override
    public ServletOutputStream getOutputStream() {
        return new CustomServletOutputStream(outputStream);
    }

    @Override
    public PrintWriter getWriter()  {
        return writer;
    }

    public void sendResponse() throws IOException {
        // 确保 writer 的内容刷新到 body 中
        writer.flush();
        setCharacterEncoding(characterEncoding);
        if(null == getContentType()){
            setContentType("text/html; charset=UTF-8");
        }
        if(null == getHeader("Content-Length")){
            setContentLength(body.size());
        }
        PrintWriter responseWriter = new PrintWriter(new OutputStreamWriter(outputStream,characterEncoding));

        // 写入状态行
        responseWriter.printf("HTTP/1.1 %d %s\r\n", statusCode, statusMessage);

        // 写入头信息
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            String headerName = entry.getKey();
            for (String headerValue : entry.getValue()) {
                responseWriter.printf("%s: %s\r\n", headerName, headerValue);
            }
        }
        // 写入 Cookie
        for (Cookie cookie : cookies) {
            StringBuilder cookieHeader = new StringBuilder();
            cookieHeader.append(cookie.getName()).append("=").append(cookie.getValue());
            if (cookie.getMaxAge() > 0) {
                cookieHeader.append("; Max-Age=").append(cookie.getMaxAge());
            }
            if (cookie.getPath() != null) {
                cookieHeader.append("; Path=").append(cookie.getPath());
            }
            if (cookie.getDomain() != null) {
                cookieHeader.append("; Domain=").append(cookie.getDomain());
            }
            responseWriter.printf("Set-Cookie: %s\r\n", cookieHeader.toString());
        }

        // 空行标识头部结束
        responseWriter.print("\r\n");
        responseWriter.flush();

        // 写入主体内容
        body.writeTo(outputStream);

        responseWriter.flush();
//        outputStream.flush();
    }

    private String getStatusMessage(int statusCode) {
        switch (statusCode) {
            case HttpServletResponse.SC_OK:
                return "OK";
            case HttpServletResponse.SC_NOT_FOUND:
                return "Not Found";
            case HttpServletResponse.SC_INTERNAL_SERVER_ERROR:
                return "Internal Server Error";
            default:
                return "Unknown Status";
        }
    }

    @Override
    public void setStatus(int sc) {
        this.statusCode = sc;
        this.statusMessage = getStatusMessage(sc);
    }

    @Override
    public void setStatus(int sc, String sm) {
        this.statusCode = sc;
        this.statusMessage = sm;
    }

    @Override
    public int getStatus() {
        return statusCode;
    }

    @Override
    public void setHeader(String name, String value) {
        List<String> values = new ArrayList<>();
        values.add(value);
        headers.put(name, values); // 覆盖之前的值
    }

    @Override
    public void addHeader(String name, String value) {
        headers.computeIfAbsent(name, k -> new ArrayList<>()).add(value); // 添加值到现有的头信息
    }

    @Override
    public String getHeader(String name) {
        List<String> values = headers.get(name);
        return (values != null && !values.isEmpty()) ? values.get(0) : null;
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return headers.getOrDefault(name, Collections.emptyList());
    }

    @Override
    public Collection<String> getHeaderNames() {
        return headers.keySet();
    }

    @Override
    public boolean containsHeader(String name) {
        return headers.containsKey(name);
    }

    // 设置整数类型的头信息（自动转换为字符串）
    @Override
    public void setIntHeader(String name, int value) {
        setHeader(name, Integer.toString(value));
    }

    // 添加整数类型的头信息
    @Override
    public void addIntHeader(String name, int value) {
        addHeader(name, Integer.toString(value));
    }

    // 设置日期类型的头信息
    @Override
    public void setDateHeader(String name, long date) {
        setHeader(name, formatDateHeader(date));
    }

    // 添加日期类型的头信息
    @Override
    public void addDateHeader(String name, long date) {
        addHeader(name, formatDateHeader(date));
    }

    // 格式化日期头信息（例如："Wed, 21 Oct 2015 07:28:00 GMT"）
    private String formatDateHeader(long timestamp) {
        return new java.text.SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US)
                .format(new Date(timestamp));
    }


    @Override
    public void addCookie(Cookie cookie) {
        cookies.add(cookie);
    }

    @Override
    public String encodeURL(String url) {
        return "";
    }

    @Override
    public String encodeRedirectURL(String url) {
        return "";
    }

    @Override
    public String encodeUrl(String url) {
        return "";
    }

    @Override
    public String encodeRedirectUrl(String url) {
        return "";
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {

    }

    @Override
    public void sendError(int sc) throws IOException {

    }

    @Override
    public void sendRedirect(String location) throws IOException {

    }


    @Override
    public void setContentType(String type) {
        setHeader("Content-Type", type);
    }

    @Override
    public void setBufferSize(int size) {

    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {

    }

    @Override
    public void resetBuffer() {

    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public void setLocale(Locale loc) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public void setContentLength(int len) {
        setHeader("Content-Length", String.valueOf(len));
    }

    @Override
    public void setCharacterEncoding(String charset) {
        setHeader("Content-Encoding", charset);
    }

    @Override
    public void setContentLengthLong(long len) {
        setHeader("Content-Length", String.valueOf(len));
    }

    @Override
    public String getCharacterEncoding() {
        return characterEncoding;
    }

    @Override public String getContentType() {
        return getHeader("Content-Type");
    }
}
