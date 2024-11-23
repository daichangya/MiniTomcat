package com.daicy.minitomcat;

public class LogManager {

    private static FileLogger logger =  new FileLogger(FileLogger.Level.INFO, "server.log");
    // 创建 Logger 实例，设置日志级别为 INFO，并指定日志文件路径

    public static FileLogger getLogger() {
        return logger;
    }

    public static void setLogger(FileLogger newLogger) {
        logger = newLogger;
    }
}
