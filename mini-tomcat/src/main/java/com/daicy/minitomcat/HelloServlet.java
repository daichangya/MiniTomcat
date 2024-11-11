package com.daicy.minitomcat;


import javax.servlet.*;

import java.io.IOException;

public class HelloServlet implements Servlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        System.out.println("HelloServlet initialized.");
    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest req, ServletResponse res)  {
        try {
            res.getWriter().println("<html><body><h1>Hello from HelloServlet!</h1></body></html>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getServletInfo() {
        return "";
    }

    @Override
    public void destroy() {
        System.out.println("HelloServlet destroyed.");
    }
}