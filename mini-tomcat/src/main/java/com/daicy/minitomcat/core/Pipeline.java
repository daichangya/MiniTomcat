package com.daicy.minitomcat.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public class Pipeline {
    private final List<Valve> valves = new ArrayList<>();
    private Valve basicValve; // BasicValve 处理最后的逻辑

    public void addValve(Valve valve) {
        valves.add(valve);
    }

    public void setBasicValve(Valve basicValve) {
        this.basicValve = basicValve;
    }

    public void invoke(HttpServletRequest request, HttpServletResponse response) {
        List<Valve> allValves = new ArrayList<>(valves);
        if (basicValve != null) {
            allValves.add(basicValve);
        }
        new ValveContext(allValves).invokeNext(request, response);
    }
}
