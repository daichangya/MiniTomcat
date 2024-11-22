//package com.daicy.minitomcat;
//
//import javax.servlet.Servlet;
//import javax.servlet.ServletConfig;
//
//
//public class ServletLoader {
//    public static Servlet loadServlet(ServletConfig config) {
//        String servletName = config.getServletName();
//        try {
//            // 通过 servletName 从 servletClassMapping 获取对应的类名
//            String servletClassName = HttpServer.parser.getServletClass(servletName);
//            if (servletClassName != null) {
//                Class<?> servletClass = Class.forName(servletClassName);
//                Servlet servlet = (Servlet) servletClass.getDeclaredConstructor().newInstance();
//
//                // 初始化 Servlet
//                servlet.init(config);
//                return servlet;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//}
