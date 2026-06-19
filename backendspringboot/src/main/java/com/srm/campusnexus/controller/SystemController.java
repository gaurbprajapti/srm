package com.srm.campusnexus.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SystemController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Server is running!");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("version", "2.0.0");
        response.put("environment", System.getProperty("spring.profiles.active", "development"));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> apiDocumentation() {
        Map<String, String> documentation = new HashMap<>();
        documentation.put("auth", "/api/user - User authentication endpoints");
        documentation.put("clubs", "/api/clubs - Club management endpoints");
        documentation.put("jobs", "/api/jobs - Job management endpoints");
        documentation.put("health", "/api/health - Health check endpoint");

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "SRM API v2.0 - JWT Authentication System (Spring Boot)");
        response.put("documentation", documentation);
        response.put("authRequired", "Include Authorization: Bearer <token> header for protected routes");

        return ResponseEntity.ok(response);
    }
}