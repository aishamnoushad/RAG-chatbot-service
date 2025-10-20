package org.cloudjune.ragchatbotservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cloudjune.ragchatbotservice.data.dtos.AddMessageRequest;
import org.cloudjune.ragchatbotservice.data.dtos.MessageResponse;
import org.cloudjune.ragchatbotservice.services.ChatMessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
