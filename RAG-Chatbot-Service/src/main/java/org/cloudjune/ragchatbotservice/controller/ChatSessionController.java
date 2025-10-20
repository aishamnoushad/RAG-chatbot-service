package org.cloudjune.ragchatbotservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cloudjune.ragchatbotservice.data.dtos.CreateSessionRequest;
import org.cloudjune.ragchatbotservice.data.dtos.PagedResponse;
import org.cloudjune.ragchatbotservice.data.dtos.SessionResponse;
import org.cloudjune.ragchatbotservice.services.ChatSessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Chat Sessions", description = "API for managing chat sessions")
public class ChatSessionController {

    private final ChatSessionService sessionService;

    @PostMapping
    @Operation(summary = "Create a new chat session", description = "Creates a new chat session for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Session created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid API key"),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded")
    })
    public ResponseEntity<SessionResponse> createSession(@Valid @RequestBody CreateSessionRequest request) {
        log.info("Creating session for user: {}", request.getUserId());
        SessionResponse response = sessionService.createSession(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all sessions for a user", description = "Retrieves all chat sessions for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sessions retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid API key"),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded")
    })
    public ResponseEntity<List<SessionResponse>> getUserSessions(
            @Parameter(description = "User ID") @PathVariable String userId) {
        log.info("Retrieving sessions for user: {}", userId);
        List<SessionResponse> sessions = sessionService.getUserSessions(userId);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/user/{userId}/paginated")
    @Operation(summary = "Get paginated sessions for a user", description = "Retrieves paginated chat sessions for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sessions retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid API key"),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded")
    })
    public ResponseEntity<PagedResponse<SessionResponse>> getUserSessionsPaginated(
            @Parameter(description = "User ID") @PathVariable String userId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.info("Retrieving paginated sessions for user: {}, page: {}, size: {}", userId, page, size);
        PagedResponse<SessionResponse> sessions = sessionService.getUserSessions(userId, page, size);
        return ResponseEntity.ok(sessions);
    }
}
