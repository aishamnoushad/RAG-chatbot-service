package org.cloudjune.ragchatbotservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
@Slf4j
@RequiredArgsConstructor
public class HealthController {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${app.version:1.0.0}")
    private String version;

    private final DataSource dataSource;

    // Lightweight endpoint to specifically report PostgreSQL connectivity
    @GetMapping("/db")
    @Operation(summary = "Health check for db", description = "Returns the health status of db")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "DB Connection successfull")
    })
    public ResponseEntity<Map<String, Object>> dbHealth() {
        Map<String, Object> body = new HashMap<>();
        body.put("component", "postgresql");
        body.put("checkedAt", OffsetDateTime.now().toString());
        try (Connection c = dataSource.getConnection()) {
            // If we got a connection without exception, consider DB UP
            body.put("status", "UP");
            body.put("databaseProduct", c.getMetaData().getDatabaseProductName());
            body.put("databaseVersion", c.getMetaData().getDatabaseProductVersion());
            return ResponseEntity.ok(body);
        } catch (SQLException ex) {
            log.warn("/health/db check failed: {}", ex.getMessage());
            body.put("status", "DOWN");
            body.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
        }
    }

    @GetMapping
    @Operation(summary = "Health check", description = "Returns the health status of the application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application is healthy")
    })
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("application", applicationName);
        health.put("version", version);
        health.put("timestamp", LocalDateTime.now());
        log.debug("Health check requested - status: UP");
        return ResponseEntity.ok(health);
    }


}