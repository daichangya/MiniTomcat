package com.daicy.minitomcat;


import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static com.daicy.minitomcat.HttpProcessor.send404Response;

public class ServletProcessor {


    public void process(HttpServletRequest request, HttpServletResponse response) {
        String uri = request.getRequestURI();
        try {
            PrintWriter writer = response.getWriter();
            WebXmlServletContainer parser = HttpServer.parser;
            String servletName = parser.getServletName(uri);
            if (null != servletName) {
                writeResponseHeaders(writer, 200, "OK");
                Servlet servlet = parser.getServlet(servletName);
                if (null == servlet){
                    ServletConfig servletConfig = parser.getServletConfig(uri);
                    servlet = ServletLoader.loadServlet(servletConfig);
                    if (null == servlet){
                        return;
                    }
                    // 将初始化后的 Servlet 存储在 WebXmlServletContainer 中，后续可通过 WebXmlServletContainer 访问
                    parser.setServlet(servletName, servlet);
                }
                servlet.service(request, response);
            } else {
                send404Response(writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeResponseHeaders(PrintWriter writer, int statusCode, String statusMessage) {
        writer.println("HTTP/1.1 " + statusCode + " " + statusMessage);
        writer.println("Content-Type: text/html; charset=UTF-8");
        writer.println();
    }

}
