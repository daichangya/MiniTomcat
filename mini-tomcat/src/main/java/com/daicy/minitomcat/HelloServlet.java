package com.daicy.minitomcat;


import com.daicy.minitomcat.servlet.HttpServletResponseImpl;

import javax.servlet.*;

import java.io.IOException;
import java.io.PrintWriter;

public class HelloServlet implements Servlet {

    private ServletConfig servletConfig;

    @Override
    public ServletConfig getServletConfig() {
        return servletConfig;
    }

    @Override
    public void init(ServletConfig servletConfig) {
        System.out.println("HelloServlet initialized.");
        this.servletConfig = servletConfig;
    }

    @Override
    public void service(ServletRequest request, ServletResponse response) {
        try {
            String greeting = servletConfig.getInitParameter("greeting");
            response.getWriter().println("<html><body><h1>" + greeting + "</h1></body></html>");
            Thread.sleep(50);
            response.getWriter().flush();

//            HttpServletResponseImpl responseImpl = (HttpServletResponseImpl) response;
//            String greeting = servletConfig.getInitParameter("greeting");
//            String html = responseImpl.getReponseString();
//            html = html + "<html><body><h1>" + greeting + "</h1></body></html>";
//            PrintWriter writer = response.getWriter();
//            writer.print(html);
//            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
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