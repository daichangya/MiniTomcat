package com.daicy.minitomcat;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static com.daicy.minitomcat.HttpProcessor.send404Response;

public class ServletProcessor {

    public void process(HttpServletRequest request, HttpServletResponse response) {
        String servletName = getServletName(request.getRequestURI());
        try {
            PrintWriter writer = response.getWriter();
            if ("HelloServlet".equals(servletName)) {
                writeResponseHeaders(writer, 200, "OK");
                HelloServlet servlet = new HelloServlet();
                servlet.service(request, response);
            } else {
                send404Response(writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getServletName(String path) {
        if ("/hello".equals(path)) {
            return "HelloServlet";
        }
        return null;
    }

    private void writeResponseHeaders(PrintWriter writer, int statusCode, String statusMessage) {
        writer.println("HTTP/1.1 " + statusCode + " " + statusMessage);
        writer.println("Content-Type: text/html; charset=UTF-8");
        writer.println();
    }

}
