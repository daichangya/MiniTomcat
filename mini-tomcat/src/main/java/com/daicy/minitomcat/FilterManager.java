package com.daicy.minitomcat;

import com.daicy.minitomcat.servlet.FilterChainImpl;

import javax.servlet.*;
import java.io.IOException;
import java.util.*;

public class FilterManager {
    private List<Filter> filters = new ArrayList<>();

    // 添加过滤器
    public void addFilter(Filter filter) {
        filters.add(filter);
    }

    // 执行过滤器链
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        FilterChain defaultChain = new FilterChainImpl(filters);
        defaultChain.doFilter(request, response);  // 递归执行过滤器链
    }

    public List<Filter> getFilters() {
        return filters;
    }
}
