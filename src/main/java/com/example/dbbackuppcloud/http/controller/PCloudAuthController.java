package com.example.dbbackuppcloud.http.controller;

import com.example.dbbackuppcloud.service.PCloudAPIAuth;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
public class PCloudAuthController {
    private PCloudAPIAuth pCloudAPIAuth;

    @GetMapping("/token")
    public String parseToken(@RequestParam("access_token") String accessToken, @RequestParam String token_type, @RequestParam String userid, @RequestParam String locationid, @RequestParam String hostname) {
        pCloudAPIAuth.finishAuth(accessToken);
        return "OK";
    }
}
