package com.daicy.panda.netty.monitoring;

import com.daicy.panda.netty.PandaStatus;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;

/**
 * 健康检查Servlet
 * 提供服务器健康状态、监控指标等信息
 *
 * @author daichangya
 */
@Slf4j
public class HealthCheckServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String path = req.getPathInfo();
        
        if (path == null || path.equals("/") || path.equals("")) {
            // 返回健康检查状态
            handleHealthCheck(req, resp);
        } else if (path.equals("/metrics")) {
            // 返回监控指标
            handleMetrics(req, resp);
        } else if (path.equals("/status")) {
            // 返回服务器状态
            handleStatus(req, resp);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * 处理健康检查请求
     */
    private void handleHealthCheck(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        PrintWriter out = resp.getWriter();
        
        // 简单的健康检查：检查服务器是否正常运行
        String status = "UP";
        
        try {
            // 可以添加更多健康检查逻辑
            // 例如：检查数据库连接、检查线程池状态等
            
            out.println("{");
            out.println("  \"status\": \"" + status + "\",");
            out.println("  \"timestamp\": \"" + System.currentTimeMillis() + "\"");
            out.println("}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{");
            out.println("  \"status\": \"DOWN\",");
            out.println("  \"error\": \"" + e.getMessage() + "\"");
            out.println("}");
        }
    }

    /**
     * 处理监控指标请求
     */
    private void handleMetrics(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        PrintWriter out = resp.getWriter();
        
        PandaStatus status = PandaStatus.get();
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        
        long heapUsed = memoryBean.getHeapMemoryUsage().getUsed();
        long heapMax = memoryBean.getHeapMemoryUsage().getMax();
        long heapCommitted = memoryBean.getHeapMemoryUsage().getCommitted();
        long nonHeapUsed = memoryBean.getNonHeapMemoryUsage().getUsed();
        
        out.println("{");
        out.println("  \"requests\": {");
        out.println("    \"total\": " + status.getTotalRequests() + ",");
        out.println("    \"handled\": " + status.getHandledRequests() + ",");
        out.println("    \"pending\": " + status.getPendingRequests());
        out.println("  },");
        out.println("  \"connections\": {");
        out.println("    \"active\": " + status.getConnections());
        out.println("  },");
        out.println("  \"threadPool\": {");
        out.println("    \"poolSize\": " + status.getWorkerPoolSize() + ",");
        out.println("    \"activeCount\": " + status.getActivePoolSize());
        out.println("  },");
        out.println("  \"memory\": {");
        out.println("    \"heapUsed\": " + heapUsed + ",");
        out.println("    \"heapMax\": " + heapMax + ",");
        out.println("    \"heapCommitted\": " + heapCommitted + ",");
        out.println("    \"nonHeapUsed\": " + nonHeapUsed + ",");
        out.println("    \"heapUsagePercent\": " + (heapMax > 0 ? (heapUsed * 100 / heapMax) : 0));
        out.println("  },");
        out.println("  \"jvm\": {");
        out.println("    \"uptime\": " + runtimeBean.getUptime() + ",");
        out.println("    \"threadCount\": " + threadBean.getThreadCount() + ",");
        out.println("    \"peakThreadCount\": " + threadBean.getPeakThreadCount());
        out.println("  }");
        out.println("}");
    }

    /**
     * 处理服务器状态请求
     */
    private void handleStatus(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();
        
        PandaStatus status = PandaStatus.get();
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        
        long heapUsed = memoryBean.getHeapMemoryUsage().getUsed();
        long heapMax = memoryBean.getHeapMemoryUsage().getMax();
        double heapUsagePercent = heapMax > 0 ? (heapUsed * 100.0 / heapMax) : 0;
        
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("    <meta charset=\"UTF-8\">");
        out.println("    <title>Panda Server Status</title>");
        out.println("    <style>");
        out.println("        body { font-family: Arial, sans-serif; margin: 20px; }");
        out.println("        table { border-collapse: collapse; width: 100%; margin: 20px 0; }");
        out.println("        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
        out.println("        th { background-color: #f2f2f2; }");
        out.println("        .metric { font-weight: bold; }");
        out.println("    </style>");
        out.println("</head>");
        out.println("<body>");
        out.println("    <h1>Panda Server Status</h1>");
        out.println("    <h2>Request Statistics</h2>");
        out.println("    <table>");
        out.println("        <tr><th>Metric</th><th>Value</th></tr>");
        out.println("        <tr><td>Total Requests</td><td>" + status.getTotalRequests() + "</td></tr>");
        out.println("        <tr><td>Handled Requests</td><td>" + status.getHandledRequests() + "</td></tr>");
        out.println("        <tr><td>Pending Requests</td><td>" + status.getPendingRequests() + "</td></tr>");
        out.println("        <tr><td>Active Connections</td><td>" + status.getConnections() + "</td></tr>");
        out.println("    </table>");
        out.println("    <h2>Thread Pool</h2>");
        out.println("    <table>");
        out.println("        <tr><th>Metric</th><th>Value</th></tr>");
        out.println("        <tr><td>Pool Size</td><td>" + status.getWorkerPoolSize() + "</td></tr>");
        out.println("        <tr><td>Active Count</td><td>" + status.getActivePoolSize() + "</td></tr>");
        out.println("    </table>");
        out.println("    <h2>Memory</h2>");
        out.println("    <table>");
        out.println("        <tr><th>Metric</th><th>Value</th></tr>");
        out.println("        <tr><td>Heap Used</td><td>" + formatBytes(heapUsed) + "</td></tr>");
        out.println("        <tr><td>Heap Max</td><td>" + formatBytes(heapMax) + "</td></tr>");
        out.println("        <tr><td>Heap Usage</td><td>" + String.format("%.2f%%", heapUsagePercent) + "</td></tr>");
        out.println("    </table>");
        out.println("    <h2>JVM</h2>");
        out.println("    <table>");
        out.println("        <tr><th>Metric</th><th>Value</th></tr>");
        out.println("        <tr><td>Uptime</td><td>" + formatDuration(runtimeBean.getUptime()) + "</td></tr>");
        out.println("    </table>");
        out.println("    <p><a href=\"/health/metrics\">JSON Metrics</a></p>");
        out.println("</body>");
        out.println("</html>");
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.2f MB", bytes / (1024.0 * 1024));
        return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
    }

    private String formatDuration(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) {
            return String.format("%d days, %d hours, %d minutes", days, hours % 24, minutes % 60);
        } else if (hours > 0) {
            return String.format("%d hours, %d minutes", hours, minutes % 60);
        } else if (minutes > 0) {
            return String.format("%d minutes, %d seconds", minutes, seconds % 60);
        } else {
            return String.format("%d seconds", seconds);
        }
    }
}

