package com.daicy.minitomcat;

import com.daicy.minitomcat.core.*;
import com.daicy.minitomcat.servlet.Request;
import com.daicy.minitomcat.servlet.Response;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;
import java.net.Socket;

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
        Request request = null;
        try  {
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            // 解析请求
            request = HttpRequestParser.parseHttpRequest(inputStream);
            // 构建响应
            Response response = new Response(outputStream);
            if(null == request){
                return;
            }
            keepAlive = parseKeepAliveHeader(request) && !isCloseConnection(request);
            Pipeline pipeline = new Pipeline();
            pipeline.addValve(new LogValve());
            pipeline.setBasicValve(new BasicValve(outputStream));
            pipeline.invoke(request, response);

        } catch (Exception e) {
            LogManager.getLogger().info("HttpProcessor error " + e.getMessage());
            e.printStackTrace();
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

    class BasicValve implements Valve {
        private OutputStream outputStream;

        public BasicValve(OutputStream outputStream) {
            this.outputStream = outputStream;
        }
        @Override
        public void invoke(HttpServletRequest request, HttpServletResponse response, ValveContext context) {
            // 默认的 Valve，处理请求
//            Request requestImpl = (Request) request;
//            Response responseImpl = (Response) response;
            String uri = request.getRequestURI();
            StandardContext standardContext = HttpServer.context;
            Wrapper wrapper = standardContext.getWrapper(uri);
            if (uri.endsWith(".html") || uri.endsWith(".css") || uri.endsWith(".js")) {
                staticProcessor.process(request, response);
            }else if (null != wrapper)  {
                // 普通请求处理
                servletProcessor.process(request, response);
            }else {
                send404Response(outputStream);
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
        writer.flush();
    }

}