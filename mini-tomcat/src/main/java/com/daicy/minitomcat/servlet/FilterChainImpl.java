package com.daicy.minitomcat.servlet;

import com.google.common.collect.Lists;

import javax.servlet.*;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.panda.netty.servlet.impl
 * @date:19-11-12
 */
public class FilterChainImpl implements FilterChain {

    private List<Filter> filters;  // 存储过滤器的列表
    private int currentIndex = 0;  // 当前过滤器的索引

    public FilterChainImpl(List<Filter> filters) {
        this.filters = filters;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        // 如果当前索引小于过滤器链的长度，执行下一个过滤器
        if (currentIndex < filters.size()) {
            Filter filter = filters.get(currentIndex);
            currentIndex++;  // 递增索引，指向下一个过滤器
            filter.doFilter(request, response, this);  // 递归调用，传入下一个过滤器
        }
    }
}
