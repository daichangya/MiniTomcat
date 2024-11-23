package com.daicy.minitomcat;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileLogger {
    // 日志级别
    public enum Level {
        DEBUG, INFO, WARN, ERROR
    }

    private Level currentLevel;
    private String logFilePath;

    // 构造函数，设置日志级别和日志文件路径
    public FileLogger(Level level, String logFilePath) {
        this.currentLevel = level;
        this.logFilePath = logFilePath;
    }

    // 获取当前时间的字符串
    private String getCurrentTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }

    // 输出日志到控制台和文件
    private void log(Level level, String message) {
        if (level.ordinal() >= currentLevel.ordinal()) {
            String logMessage = String.format("[%s] [%s] %s", getCurrentTime(), level, message);
            // 输出到控制台
            System.out.println(logMessage);
            // 输出到文件
            try (FileWriter writer = new FileWriter(logFilePath, true)) {
                writer.write(logMessage + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // debug级别日志
    public void debug(String message) {
        log(Level.DEBUG, message);
    }

    // info级别日志
    public void info(String message) {
        log(Level.INFO, message);
    }

    // warn级别日志
    public void warn(String message) {
        log(Level.WARN, message);
    }

    // error级别日志
    public void error(String message) {
        log(Level.ERROR, message);
    }
}