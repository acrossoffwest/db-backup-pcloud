package com.example.dbbackuppcloud.service;

import com.pcloud.sdk.ApiClient;
import com.pcloud.sdk.Authenticators;
import com.pcloud.sdk.PCloudSdk;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class PCloudAPIClientFactory {
    private Map<String, ApiClient> clients = new ConcurrentHashMap<>();

    public ApiClient getClient(String accessToken) {
        log.info(accessToken);
        var client = clients.get(accessToken);
        if (client != null) {
            return client;
        }
        client = createClient(accessToken);
        clients.put(accessToken, client);
        return client;
    }

    public ApiClient createClient(String accessToken) {
        return PCloudSdk.newClientBuilder()
                .authenticator(Authenticators.newOAuthAuthenticator(accessToken))
                .apiHost("eapi.pcloud.com")
                .create();
    }
}
