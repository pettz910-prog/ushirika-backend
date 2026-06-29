package com.mdau.ushirika.common.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * Lightweight health probe for Railway's healthcheck — responds before the JPA
 * context has fully warmed so the platform doesn't time out during cold start.
 * Path: GET /api/health  (context-path is /api)
 */
@RestController
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "ushirika-backend",
                "timestamp", Instant.now().toString()
        ));
    }
}
