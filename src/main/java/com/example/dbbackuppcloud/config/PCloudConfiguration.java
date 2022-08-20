package com.example.dbbackuppcloud.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pcloud.oauth")
public record PCloudConfiguration(
    String clientID,
    String callbackUrl
) {}
