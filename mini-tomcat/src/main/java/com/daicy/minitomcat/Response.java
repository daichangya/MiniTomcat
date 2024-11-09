package com.daicy.minitomcat;

import java.io.*;
import java.net.URL;

import static com.daicy.minitomcat.HttpServer.WEB_ROOT;

public class Response {
    private OutputStream outputStream;

    public Response(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }
}