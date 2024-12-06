package com.daicy.minitomcat.log;

import java.io.FileWriter;
import java.io.IOException;


public class FileLogger extends AbstractLogger {
    private final String logFilePath;

    public FileLogger(LogLevel minLogLevel, String logFilePath) {
        super(minLogLevel);
        this.logFilePath = logFilePath;
    }


    @Override
    public void log(LogLevel level, String message) {
        if (level.ordinal() >= minLogLevel.ordinal()) {
            String logMessage = String.format("[%s] [%s] %s", getCurrentTime(), level, message);
            // 输出到文件
            try (FileWriter writer = new FileWriter(logFilePath, true)) {
                writer.write(logMessage + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}