package com.example.dbbackuppcloud.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "notification")
public record NotificationConfiguration(
        String telegramNotificationServiceUrl,
        int telegramChannelId
) {}
