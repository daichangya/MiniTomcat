package com.daicy.minitomcat;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HelloServlet {

    public void service(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.getWriter().println("<html><body><h1>Hello from HelloServlet!</h1></body></html>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}