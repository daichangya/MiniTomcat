package com.daicy.minitomcat;

import java.io.*;
import java.net.URL;

import static com.daicy.minitomcat.HttpServer.WEB_ROOT;

public class StaticResourceProcessor {
    public void process(Request request, Response response) {
        try {

            OutputStream outputStream = response.getOutputStream();
            // 查找请求的静态文件
            String path = request.getUri();
            URL url = HttpServer.class.getClassLoader().getResource(WEB_ROOT+ path);
            if(null == url){
                sendResponse(outputStream, 404, "Not Found", "The requested resource was not found.");
                return;
            }
            File file = new File(url.getPath());
            if (file.exists() && !file.isDirectory()) {
                sendFileResponse(outputStream, file);
            } else {
                sendResponse(outputStream, 404, "Not Found", "The requested resource was not found.");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    // 发送普通文本响应
    private static void sendResponse(OutputStream outputStream, int statusCode, String statusText, String message) throws IOException {
        PrintWriter writer = new PrintWriter(outputStream, true);
        writer.println("HTTP/1.1 " + statusCode + " " + statusText);
        writer.println("Content-Type: text/html; charset=UTF-8");
        writer.println();
        writer.println("<html><body><h1>" + statusCode + " " + statusText + "</h1><p>" + message + "</p></body></html>");
    }

    // 发送文件响应
    private static void sendFileResponse(OutputStream outputStream, File file) throws IOException {
        PrintWriter writer = new PrintWriter(outputStream, true);
        writer.println("HTTP/1.1 200 OK");
        writer.println("Content-Type: " + getContentType(file));
        writer.println("Content-Length: " + file.length());
        writer.println();

        // 发送文件内容
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    // 根据文件后缀返回 Content-Type
    private static String getContentType(File file) {
        String name = file.getName().toLowerCase();
        if (name.endsWith(".html") || name.endsWith(".htm")) {
            return "text/html";
        } else if (name.endsWith(".css")) {
            return "text/css";
        } else if (name.endsWith(".js")) {
            return "application/javascript";
        } else if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (name.endsWith(".png")) {
            return "image/png";
        } else {
            return "application/octet-stream";
        }
    }
}
