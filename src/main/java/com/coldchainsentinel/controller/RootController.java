package com.coldchainsentinel.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RootController {

    @GetMapping("/")
    public Map<String, Object> root() {
        return Map.of(
                "service", "coldchain-sentinel",
                "status", "UP",
                "docs", "/swagger-ui.html",
                "health", "/api/v1/status"
        );
    }
}
