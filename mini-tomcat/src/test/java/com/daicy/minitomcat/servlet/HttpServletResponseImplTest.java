package com.daicy.minitomcat.servlet;

import org.junit.Test;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class HttpServletResponseImplTest {

    @Test
    public void testCreate() throws IOException {
        // 将完整响应写入到控制台（可替换为实际网络输出流）
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HttpServletResponseImpl response = new HttpServletResponseImpl(outputStream);

        // 设置响应状态码和头信息
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader("Content-Type", "text/html");

        // 添加 Cookie
        Cookie sessionCookie = new Cookie("JSESSIONID", "12345");
        sessionCookie.setPath("/");
        response.addCookie(sessionCookie);

        response.sendResponse();

        // 写入响应内容
        OutputStream responseBody = response.getOutputStream();
        responseBody.write("<html><body><h1>Hello, World!</h1></body></html>".getBytes());

        System.out.println(outputStream.toString());
    }
}