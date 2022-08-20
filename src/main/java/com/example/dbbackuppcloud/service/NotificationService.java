package com.example.dbbackuppcloud.service;

import com.example.dbbackuppcloud.config.NotificationConfiguration;
import com.example.dbbackuppcloud.dto.Notification;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class NotificationService {
    private WebClient webClient;
    private NotificationConfiguration notificationConfiguration;

    public void send(String message) {
        sendMono(message).block();
    }

    @SneakyThrows
    public Mono<String> sendMono(String message) {
        var receivers = List.of(notificationConfiguration.telegramChannelId());
        var notification = Notification.builder()
                .message(message)
                .receivers(receivers)
                .build();
        return webClient
                .post()
                .uri(new URI(notificationConfiguration.telegramNotificationServiceUrl()))
                .bodyValue(notification)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(exception -> log.error("Notification failed: ", exception));
    }
}
