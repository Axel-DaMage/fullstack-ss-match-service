package com.sanosysalvos.matchservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

@Component
public class AppConfig {

    private static AppConfig instance;

    @Value("${spring.application.name:match-service}")
    private String serviceName;

    @Value("${server.port:3003}")
    private int port;

    @Value("${spring.datasource.url:jdbc:mysql://localhost:3306/matchservice}")
    private String databaseUrl;

    @Value("${logging.level.com.sanosysalvos:INFO}")
    private String logLevel;

    @Value("${pet.service.url:http://pet-service:3001}")
    private String petServiceUrl;

    @Value("${location.service.url:http://geo-service:3002}")
    private String locationServiceUrl;

    @Value("${matching.min-percentage:50}")
    private int minMatchPercentage;

    @Value("${matching.auto-enabled:false}")
    private boolean autoMatchingEnabled;

    @Value("${matching.cache.enabled:true}")
    private boolean cacheEnabled;

    private AppConfig() {
    }

    @PostConstruct
    public void init() {
        instance = this;
    }

    public static AppConfig getInstance() {
        if (instance == null) {
            throw new IllegalStateException("AppConfig not initialized");
        }
        return instance;
    }

    public String getServiceName() {
        return serviceName;
    }

    public int getPort() {
        return port;
    }

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public String getPetServiceUrl() {
        return petServiceUrl;
    }

    public String getLocationServiceUrl() {
        return locationServiceUrl;
    }

    public int getMinMatchPercentage() {
        return minMatchPercentage;
    }

    public boolean isAutoMatchingEnabled() {
        return autoMatchingEnabled;
    }

    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public String getServiceInfo() {
        return String.format("Service: %s, Port: %d, AutoMatching: %s",
            serviceName, port, autoMatchingEnabled ? "enabled" : "disabled");
    }
}