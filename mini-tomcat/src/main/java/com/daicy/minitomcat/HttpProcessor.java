package com.daicy.minitomcat;

import com.daicy.minitomcat.servlet.HttpServletRequestImpl;
import com.daicy.minitomcat.servlet.HttpServletResponseImpl;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpProcessor implements Runnable{
    private Socket socket;

    private final static  ServletProcessor servletProcessor = new ServletProcessor();

    private final static  StaticResourceProcessor staticProcessor = new StaticResourceProcessor();

    public HttpProcessor(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        // 处理请求并返回响应
        process();
    }

    public void process() {
        boolean keepAlive = false;
        HttpServletRequestImpl request = null;
        try  {
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            // 解析请求
            request = HttpRequestParser.parseHttpRequest(inputStream);
            // 构建响应
            HttpServletResponseImpl response = new HttpServletResponseImpl(outputStream);
            if(null == request){
                return;
            }
            keepAlive = parseKeepAliveHeader(request) && !isCloseConnection(request);
            String uri = request.getRequestURI();
            WebXmlServletContainer parser = HttpServer.parser;
            String servletName = parser.getServletName(uri);
            if (uri.endsWith(".html") || uri.endsWith(".css") || uri.endsWith(".js")) {
                staticProcessor.process(request, response);
            }else if (null != servletName)  {
                // 普通请求处理
                servletProcessor.process(request, response);
            }else {
                send404Response(outputStream);
            }

        } catch (Exception e) {
            System.out.println("HttpProcessor error " + e.getMessage());
        } finally {
            try {
                // 如果是 keep-alive，连接保持打开，否则关闭连接
                if (!keepAlive && (null!= request && !request.isAsyncSupported())) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean parseKeepAliveHeader(HttpServletRequest request) {
        String connectionHeader = request.getHeader("Connection");
        return connectionHeader != null && connectionHeader.equalsIgnoreCase("keep-alive");
    }

    public boolean isCloseConnection(HttpServletRequest request) {
        String connectionHeader = request.getHeader("Connection");
        return connectionHeader != null && connectionHeader.equalsIgnoreCase("close");
    }

    static void send404Response(OutputStream outputStream) {
        sendResponse(outputStream, 404, "Not Found", "The requested resource was not found.");
    }


    // 发送普通文本响应
    private static void sendResponse(OutputStream outputStream, int statusCode, String statusText, String message)  {
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream));
        String html = "<html><body><h1>" + statusCode + " " + statusText + "</h1><p>" + message + "</p></body></html>";
        writer.println("HTTP/1.1 " + statusCode + " " + statusText);
        writer.println("Content-Type: text/html; charset=UTF-8");
        writer.println("Content-Length: " + html.length());
        writer.println();
        writer.println(html);
    }

}