package com.daicy.minitomcat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpConnector implements Runnable {
    private static final int PORT = 8080;

    private static ThreadPool threadPool = new ThreadPool(10);  // 创建一个线程池，最多支持 10 个并发请求

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            LogManager.getLogger().info("HTTP Connector is running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                LogManager.getLogger().info("Accepted connection from " + clientSocket.getInetAddress());

                // 将连接交给 HttpProcessor 处理
                HttpProcessor processor = new HttpProcessor(clientSocket);
                threadPool.submitTask(processor);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}