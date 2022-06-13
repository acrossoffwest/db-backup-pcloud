package com.example.dbbackuppcloud.service;

import com.example.dbbackuppcloud.config.PCloudConfiguration;
import com.example.dbbackuppcloud.dto.Notification;
import com.pcloud.sdk.ApiClient;
import com.pcloud.sdk.Authenticators;
import com.pcloud.sdk.PCloudSdk;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class PCloudAPIAuth {
    @NonNull
    private PCloudConfiguration pCloudConfig;
    @NonNull
    private WebClient webClient;
    private String accessToken;
    private boolean authBegun = false;

    private String getAuthUrl() {
        return "https://my.pcloud.com/oauth2/authorize?client_id=%s&response_type=token&redirect_uri=%s".formatted(pCloudConfig.clientID(), pCloudConfig.callbackUrl());
    }

    @SneakyThrows
    public void startAuth() {
        var message = "Please open link shown below and authorize: [authorize](%s)".formatted(getAuthUrl());
        log.info(message);
        var response = webClient
                .post()
                .uri(new URI("https://ts3-telegram.aow.space/notifications"))
                .bodyValue(Notification.builder().message(message).receivers(List.of(236368989)).build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
        log.info(response);
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
