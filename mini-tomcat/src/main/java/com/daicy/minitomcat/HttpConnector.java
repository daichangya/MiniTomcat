package com.daicy.minitomcat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpConnector implements Runnable {
    private static final int PORT = 8080;

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("HTTP Connector is running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from " + clientSocket.getInetAddress());

                // 将连接交给 HttpProcessor 处理
                HttpProcessor processor = new HttpProcessor(clientSocket);
                processor.process();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}