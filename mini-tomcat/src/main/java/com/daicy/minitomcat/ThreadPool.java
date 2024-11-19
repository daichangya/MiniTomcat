package com.daicy.minitomcat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadPool {
    private ExecutorService executor;

    public ThreadPool(int poolSize) {
        executor = Executors.newFixedThreadPool(poolSize);
    }

    public void submitTask(Runnable task) {
        executor.submit(task);
    }

    public void shutdown() {
        if (executor != null) {
            executor.shutdown();
        }
    }

    public boolean isShutdown() {
        return executor.isShutdown();
    }

    public ThreadPoolExecutor getExecutor() {
        return (ThreadPoolExecutor) executor;
    }
}