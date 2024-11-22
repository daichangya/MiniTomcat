package com.daicy.minitomcat;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.daicy.minitomcat.servlet.ServletConfigImpl;
import org.w3c.dom.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletConfig;


public class WebXmlServletContainer {

    private  Map<String, ServletConfig> servletConfigMap = new HashMap<>();

    private ServletContext servletContext;

    private List<String> servletNames = new ArrayList<>();

    public void loadConfig(String xmlPath) {
        try {
            this.servletContext = HttpServer.servletContext;

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(getClass().getResourceAsStream(xmlPath));

            NodeList servletNodes = doc.getElementsByTagName("servlet");
            for (int i = 0; i < servletNodes.getLength(); i++) {
                Element servletElement = (Element) servletNodes.item(i);
                String servletName = servletElement.getElementsByTagName("servlet-name").item(0).getTextContent();
                String servletClass = servletElement.getElementsByTagName("servlet-class").item(0).getTextContent();

                Map<String, String> initParamsMap = new HashMap<>();
                NodeList initParams = servletElement.getElementsByTagName("init-param");
                for (int j = 0; j < initParams.getLength(); j++) {
                    Element param = (Element) initParams.item(j);
                    String paramName = param.getElementsByTagName("param-name").item(0).getTextContent();
                    String paramValue = param.getElementsByTagName("param-value").item(0).getTextContent();
                    initParamsMap.put(paramName, paramValue);
                }
                ServletConfig servletConfig = new ServletConfigImpl(servletName, servletContext, initParamsMap);
                servletConfigMap.put(servletClass, servletConfig);
                servletNames.add(servletName);
            }

            NodeList mappingNodes = doc.getElementsByTagName("servlet-mapping");
            for (int i = 0; i < mappingNodes.getLength(); i++) {
                Element mappingElement = (Element) mappingNodes.item(i);
                String servletName = mappingElement.getElementsByTagName("servlet-name").item(0).getTextContent();
                String urlPattern = mappingElement.getElementsByTagName("url-pattern").item(0).getTextContent();
                servletContext.setAttribute(urlPattern, servletName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getServletName(String urlPattern) {
        return (String) servletContext.getAttribute(urlPattern);
    }

    public Map<String, ServletConfig> getServletConfigMap() {
    	return servletConfigMap;
    }

    public List<String> getServletNames() {
    	return servletNames;
    }
}