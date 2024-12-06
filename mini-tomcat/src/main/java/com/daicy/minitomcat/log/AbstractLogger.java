package com.daicy.minitomcat.log;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class AbstractLogger implements Logger{

    final LogLevel minLogLevel;

    protected AbstractLogger(LogLevel minLogLevel) {
        this.minLogLevel = minLogLevel;
    }

    private String throwableToString(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append("\tat ").append(element).append("\n");
        }
        return sb.toString();
    }


    String getCurrentTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }


    @Override
    public void log(LogLevel level, String message, Throwable throwable) {
        if (level.ordinal() >= minLogLevel.ordinal()) {
            String logMessage = message +  "\n" + throwableToString(throwable);
            log(level, logMessage);
        }
    }

    // debug级别日志
    @Override
    public void debug(String message) {
        log(LogLevel.DEBUG, message);
    }

    // info级别日志
    @Override
    public void info(String message) {
        log(LogLevel.INFO, message);
    }

    // warn级别日志
    @Override
    public void warn(String message) {
        log(LogLevel.WARN, message);
    }

    // error级别日志
    @Override
    public void error(String message) {
        log(LogLevel.ERROR, message);
    }
}
