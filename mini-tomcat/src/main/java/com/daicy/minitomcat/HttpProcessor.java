package com.daicy.minitomcat;

import com.daicy.minitomcat.servlet.HttpServletRequestImpl;
import com.daicy.minitomcat.servlet.HttpServletResponseImpl;

import java.io.*;
import java.net.Socket;

public class HttpProcessor {
    private Socket socket;

    private final static  ServletProcessor processor = new ServletProcessor();

    private final static  StaticResourceProcessor staticProcessor = new StaticResourceProcessor();

    public HttpProcessor(Socket socket) {
        this.socket = socket;
    }

    public void process() {
        try (InputStream inputStream = socket.getInputStream();
             OutputStream outputStream = socket.getOutputStream()) {

            // 解析请求
            HttpServletRequestImpl request = parseRequest(inputStream);

            // 构建响应
            HttpServletResponseImpl response = new HttpServletResponseImpl(outputStream);
            if(null == request){
                return;
            }
            String uri = request.getRequestURI();
            if (uri.endsWith(".html") || uri.endsWith(".css") || uri.endsWith(".js")) {
                staticProcessor.process(request, response);
            } else {
                processor.process(request, response);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private HttpServletRequestImpl parseRequest(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            return null;
        }

        System.out.println("Request Line: " + requestLine);
        String[] parts = requestLine.split(" ");
        String method = parts[0];
        String path = parts[1];

        return new HttpServletRequestImpl(method, path);
    }

    static void send404Response(PrintWriter writer) {
        sendResponse(writer, 404, "Not Found", "The requested resource was not found.");
    }


    // 发送普通文本响应
    private static void sendResponse(PrintWriter writer, int statusCode, String statusText, String message)  {
        String html = "<html><body><h1>" + statusCode + " " + statusText + "</h1><p>" + message + "</p></body></html>";
        writer.println("HTTP/1.1 " + statusCode + " " + statusText);
        writer.println("Content-Type: text/html; charset=UTF-8");
        writer.println("Content-Length: " + html.length());
        writer.println();
        writer.println(html);
    }

}