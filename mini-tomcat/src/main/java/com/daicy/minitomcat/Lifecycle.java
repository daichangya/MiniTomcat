package com.daicy.minitomcat;

public interface Lifecycle {
    void start() throws Exception;  // 启动操作
    void stop() throws Exception;   // 停止操作
}