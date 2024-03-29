package com.example.dbbackuppcloud.http.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@AllArgsConstructor
public class PCloudAuthRedirectController {
    @GetMapping("/")
    public String parseToken() {
        return "index.html";
    }
}
