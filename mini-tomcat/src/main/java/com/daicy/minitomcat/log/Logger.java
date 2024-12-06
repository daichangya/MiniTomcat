package com.daicy.minitomcat.log;

public interface Logger {

    void log(LogLevel level, String message);

    void log(LogLevel level, String message, Throwable throwable);

    // debug级别日志
    void debug(String message);

    // info级别日志
    void info(String message) ;

    // warn级别日志
    void warn(String message);

    // error级别日志
    void error(String message);
}
