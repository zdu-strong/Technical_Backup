package com.springboot.project.properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ServerAddressProperties {

    @Autowired
    private ServletWebServerApplicationContext servletWebServerApplicationContext;

    public String getServerAddress() {
        var port = servletWebServerApplicationContext.getWebServer().getPort();
        var serverAddress = "http://127.0.0.1:" + port;
        return serverAddress;
    }

    public String getWebSocketServerAddress() {
        var port = servletWebServerApplicationContext.getWebServer().getPort();
        var serverAddress = "ws://127.0.0.1:" + port;
        return serverAddress;
    }

}
