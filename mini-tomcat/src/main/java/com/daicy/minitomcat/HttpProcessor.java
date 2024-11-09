package com.daicy.minitomcat;

import java.io.*;
import java.net.Socket;

public class HttpProcessor {
    private Socket socket;

    private StaticResourceProcessor staticProcessor = new StaticResourceProcessor();


    public HttpProcessor(Socket socket) {
        this.socket = socket;
    }

    public void process() {
        try (InputStream inputStream = socket.getInputStream();
             OutputStream outputStream = socket.getOutputStream()) {

            // 解析请求
            Request request = parseRequest(inputStream);

            // 构建响应
            Response response = new Response(outputStream);
            if(null == request){
                return;
            }
            String uri = request.getUri();
            if (uri.endsWith(".html") || uri.endsWith(".css") || uri.endsWith(".js")) {
                staticProcessor.process(request, response);
            } else {
                staticProcessor.process(request, response);
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

    private Request parseRequest(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            return null;
        }

        System.out.println("Request Line: " + requestLine);
        String[] parts = requestLine.split(" ");
        String method = parts[0];
        String path = parts[1];

        return new Request(method, path);
    }

}