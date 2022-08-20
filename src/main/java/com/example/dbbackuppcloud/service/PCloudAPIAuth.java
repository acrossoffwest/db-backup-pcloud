package com.example.dbbackuppcloud.service;

import com.example.dbbackuppcloud.config.PCloudConfiguration;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PCloudAPIAuth {
    @NonNull
    private PCloudConfiguration pCloudConfig;
    @NonNull
    private NotificationService notificationService;
    private String accessToken;
    private boolean authBegun = false;

    private String getAuthUrl() {
        return "https://my.pcloud.com/oauth2/authorize?client_id=%s&response_type=token&redirect_uri=%s".formatted(pCloudConfig.clientID(), pCloudConfig.callbackUrl());
    }

    @SneakyThrows
    public void startAuth() {
        notificationService.send("Please open link shown below and authorize: [authorize](%s)".formatted(getAuthUrl()));
        authBegun = true;
    }

    public void finishAuth(String code) {
        accessToken = code;
        authBegun = false;
    }

    public boolean isAuthorized() {
        return accessToken != null;
    }

    public boolean isAuthorizationBegun() {
        return authBegun;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
