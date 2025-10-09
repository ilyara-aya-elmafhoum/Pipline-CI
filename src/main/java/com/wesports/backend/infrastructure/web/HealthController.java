package com.wesports.backend.infrastructure.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Health Check", description = "System health and monitoring endpoints")
public class HealthController {

    @GetMapping("/health")
    @Operation(summary = "Check application health", description = "Returns the current health status of the WeSports application")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Application is healthy")
    })
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Application is running successfully");
        response.put("timestamp", System.currentTimeMillis());
        response.put("service", "wesports-auth");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test")
    @Operation(summary = "Test endpoint", description = "Simple test endpoint to verify API connectivity")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Test endpoint is working")
    })
    public ResponseEntity<Map<String, Object>> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Test endpoint is working");
        response.put("status", "success");
        response.put("timestamp", System.currentTimeMillis());
        response.put("version", "1.0.0");
        return ResponseEntity.ok(response);
    }
}
