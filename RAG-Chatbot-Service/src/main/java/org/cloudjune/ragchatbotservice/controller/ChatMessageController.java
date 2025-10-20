package org.cloudjune.ragchatbotservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cloudjune.ragchatbotservice.data.dtos.AddMessageRequest;
import org.cloudjune.ragchatbotservice.data.dtos.MessageResponse;
import org.cloudjune.ragchatbotservice.data.dtos.PagedResponse;
import org.cloudjune.ragchatbotservice.services.ChatMessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Chat Messages", description = "API for managing chat messages")
public class ChatMessageController {

    private final ChatMessageService messageService;

    @PostMapping
    @Operation(summary = "Add a message to a session", description = "Adds a new message to an existing chat session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Message added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Session not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid API key"),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded")
    })
    public ResponseEntity<MessageResponse> addMessage(@Valid @RequestBody AddMessageRequest request) {
        log.info("Adding message to session: {}", request.getSessionId());
        MessageResponse response = messageService.addMessage(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/session/{sessionId}")
    @Operation(summary = "Get all messages for a session", description = "Retrieves all messages for a specific chat session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Messages retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid API key"),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded")
    })
    public ResponseEntity<List<MessageResponse>> getSessionMessages(
            @Parameter(description = "Session ID") @PathVariable Long sessionId) {
        log.info("Retrieving messages for session: {}", sessionId);
        List<MessageResponse> messages = messageService.getSessionMessages(sessionId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/session/{sessionId}/paginated")
    @Operation(summary = "Get paginated messages for a session", description = "Retrieves paginated messages for a specific chat session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Messages retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid API key"),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded")
    })
    public ResponseEntity<PagedResponse<MessageResponse>> getSessionMessagesPaginated(
            @Parameter(description = "Session ID") @PathVariable Long sessionId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        log.info("Retrieving paginated messages for session: {}, page: {}, size: {}", sessionId, page, size);
        PagedResponse<MessageResponse> messages = messageService.getSessionMessages(sessionId, page, size);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/session/{sessionId}/user/{userId}")
    @Operation(summary = "Get messages for a session with user validation", description = "Retrieves messages for a session ensuring user ownership")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Messages retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Session not found or user not authorized"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid API key"),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded")
    })
    public ResponseEntity<List<MessageResponse>> getSessionMessagesWithUser(
            @Parameter(description = "Session ID") @PathVariable Long sessionId,
            @Parameter(description = "User ID") @PathVariable String userId) {
        log.info("Retrieving messages for session: {} and user: {}", sessionId, userId);
        List<MessageResponse> messages = messageService.getSessionMessages(sessionId, userId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/session/{sessionId}/user/{userId}/paginated")
    @Operation(summary = "Get paginated messages for a session with user validation", description = "Retrieves paginated messages for a session ensuring user ownership")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Messages retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Session not found or user not authorized"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid API key"),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded")
    })
    public ResponseEntity<PagedResponse<MessageResponse>> getSessionMessagesWithUserPaginated(
            @Parameter(description = "Session ID") @PathVariable Long sessionId,
            @Parameter(description = "User ID") @PathVariable String userId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        log.info("Retrieving paginated messages for session: {}, user: {}, page: {}, size: {}", sessionId, userId, page, size);
        PagedResponse<MessageResponse> messages = messageService.getSessionMessages(sessionId, userId, page, size);
        return ResponseEntity.ok(messages);
    }

    @DeleteMapping("/{sessionId}/user/{userId}")
    @Operation(summary = "Delete a session", description = "Deletes a chat session and all its messages")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Session deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Session not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid API key"),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded")
    })
    public ResponseEntity<Void> deleteMessages(
            @Parameter(description = "Session ID") @PathVariable Long sessionId,
            @Parameter(description = "User ID") @PathVariable String userId) {
        log.info("Deleting session: {} for user: {}", sessionId, userId);
        messageService.deleteMessage(sessionId, userId);
        return ResponseEntity.noContent().build();
    }
}
