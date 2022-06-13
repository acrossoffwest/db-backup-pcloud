package com.example.dbbackuppcloud.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Slf4j
@ConfigurationProperties(prefix = "pcloud.oauth")
public record PCloudConfiguration(
    String clientID,
    String callbackUrl
) {}
