package com.daicy.minitomcat;

public class Request {
    private String method;
    private String uri;

    public Request(String method, String uri) {
        this.method = method;
        this.uri = uri;
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }
}