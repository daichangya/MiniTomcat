package com.daicy.minitomcat.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Valve {
    void invoke(HttpServletRequest request, HttpServletResponse response, ValveContext context);
}
