package com.daicy.minitomcat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class ValveContext {
    private int currentIndex = -1;
    private final List<Valve> valves;

    public ValveContext(List<Valve> valves) {
        this.valves = valves;
    }

    public void invokeNext(HttpServletRequest request, HttpServletResponse response) {
        currentIndex++;
        if (currentIndex < valves.size()) {
            valves.get(currentIndex).invoke(request, response, this);
        }
    }
}
