package com.daicy.minitomcat.log;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ConsoleLogger extends AbstractLogger {

    public ConsoleLogger(LogLevel minLogLevel) {
        super(minLogLevel);
    }

    @Override
    public void log(LogLevel level, String message) {
        if (level.ordinal() >= minLogLevel.ordinal()) {
            System.out.println(formatLog(level, message));
        }
    }

    @Override
    public void log(LogLevel level, String message, Throwable throwable) {
        if (level.ordinal() >= minLogLevel.ordinal()) {
            System.out.println(formatLog(level, message));
            throwable.printStackTrace(System.out);
        }
    }

    private String formatLog(LogLevel level, String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return String.format("[%s] [%s] %s", timestamp, level, message);
    }
}