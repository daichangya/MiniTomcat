package com.daicy.minitomcat.log;

import java.util.ArrayList;
import java.util.List;

public class LogManager  {
    private static final List<Logger> loggers = new ArrayList<>();

    public static void addLogger(Logger logger) {
        loggers.add(logger);
    }

    public static void log(LogLevel level, String message) {
        for (Logger logger : loggers) {
            logger.log(level, message);
        }
    }

    public static void log(LogLevel level, String message, Throwable throwable) {
        for (Logger logger : loggers) {
            logger.log(level, message, throwable);
        }
    }


    // debug级别日志
    public static void  debug(String message) {
        log(LogLevel.DEBUG, message);
    }

    // info级别日志
    public static void  info(String message) {
        log(LogLevel.INFO, message);
    }

    // warn级别日志
    public static void  warn(String message) {
        log(LogLevel.WARN, message);
    }

    // error级别日志
    public static void  error(String message) {
        log(LogLevel.ERROR, message);
    }
}