package com.daicy.minitomcat;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ServletProcessor {

    public void process(HttpServletRequest request, HttpServletResponse response) {
        String servletName = getServletName(request.getRequestURI());
        try {
            PrintWriter writer = response.getWriter();
            if ("HelloServlet".equals(servletName)) {
                writeResponseHeadersOrBody(writer, 200, "OK",null);
                HelloServlet servlet = new HelloServlet();
                servlet.service(request, response);
            } else {
                writeResponseHeadersOrBody(writer, 404, "Not Found", "The requested resource was not found.");
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

    private void writeResponseHeadersOrBody(PrintWriter writer, int statusCode, String statusMessage,
                                            String html) {
        writer.println("HTTP/1.1 " + statusCode + " " + statusMessage);
        writer.println("Content-Type: text/html; charset=UTF-8");
        writer.println();
        if(html != null){
            writer.println(html);
        }
    }

}
