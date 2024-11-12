package com.daicy.minitomcat;

import com.daicy.minitomcat.servlet.HttpServletRequestImpl;

import javax.servlet.http.Cookie;
import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestParser {
    public static HttpServletRequestImpl parseHttpRequest(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        // 读取请求行
        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            System.out.println(reader.readLine());
            throw new IOException("Empty request line");
        }

        // 解析请求行
        String[] parts = requestLine.split(" ");
        if (parts.length < 3) {
            throw new IOException("Invalid request line: " + requestLine);
        }
        String method = parts[0];
        String uri = parts[1];
        int queryIndex = uri.indexOf('?');
        String requestURI = (queryIndex >= 0) ? uri.substring(0, queryIndex) : uri;
        String queryString = (queryIndex >= 0) ? uri.substring(queryIndex + 1) : null;

        // 读取并解析 headers
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            int separatorIndex = line.indexOf(": ");
            if (separatorIndex != -1) {
                String headerName = line.substring(0, separatorIndex);
                String headerValue = line.substring(separatorIndex + 2);
                headers.put(headerName, headerValue);
            }
        }

        // 创建并返回 HttpServletRequestImpl
        return new HttpServletRequestImpl(method, requestURI, queryString, headers);
    }

    public static void main(String[] args) throws IOException {
        // 示例 HTTP 请求
        String httpRequest = "GET /hello?name=world HTTP/1.1\r\n" +
                "Host: localhost\r\n" +
                "User-Agent: TestAgent\r\n" +
                "Accept: */*\r\n" +
                "Cookie: sessionId=abc123; theme=light\r\n\r\n";
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());

        HttpServletRequestImpl request = parseHttpRequest(inputStream);

        // 输出解析后的信息
        System.out.println("Method: " + request.getMethod());
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Query String: " + request.getQueryString());
        System.out.println("Session ID: " + request.getSession().getId());
        System.out.println("Cookies:");
        for (Cookie cookie : request.getCookies()) {
            System.out.println("  " + cookie.getName() + "=" + cookie.getValue());
        }
    }
}
