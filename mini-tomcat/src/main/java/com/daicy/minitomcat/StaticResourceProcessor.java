package com.daicy.minitomcat;

import com.daicy.minitomcat.servlet.Request;
import com.daicy.minitomcat.servlet.Response;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;

import static com.daicy.minitomcat.HttpProcessor.send404Response;
import static com.daicy.minitomcat.HttpServer.WEB_ROOT;

public class StaticResourceProcessor {
    public void process(HttpServletRequest request, HttpServletResponse response) {
        try {

            OutputStream outputStream = response.getOutputStream();
            PrintWriter writer = response.getWriter();
            // 查找请求的静态文件
            String path = request.getRequestURI();
            URL url = HttpServer.class.getClassLoader().getResource(WEB_ROOT+ path);
            if(null == url){
                send404Response(outputStream);
                return;
            }
            File file = new File(url.getPath());
            if (file.exists() && !file.isDirectory()) {
                sendFileResponse(outputStream, file);
            } else {
                send404Response(outputStream);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
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