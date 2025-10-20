package org.cloudjune.ragchatbotservice.services;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cloudjune.ragchatbotservice.data.dtos.AddMessageRequest;
import org.cloudjune.ragchatbotservice.data.dtos.MessageResponse;
import org.cloudjune.ragchatbotservice.data.entities.ChatMessage;
import org.cloudjune.ragchatbotservice.data.entities.ChatSession;
import org.cloudjune.ragchatbotservice.data.mapper.ChatMessageMapper;
import org.cloudjune.ragchatbotservice.data.repositories.ChatMessageRepository;
import org.cloudjune.ragchatbotservice.data.repositories.ChatSessionRepository;
import org.cloudjune.ragchatbotservice.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChatMessageService {

    private final ChatMessageRepository messageRepository;
    private final ChatSessionRepository sessionRepository;
    private final ChatMessageMapper messageMapper;

    public MessageResponse addMessage(AddMessageRequest request) {
        log.info("Adding message to session: {}", request.getSessionId());

        ChatSession session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with id: " + request.getSessionId()));

        ChatMessage message = ChatMessage.builder()
                .chatSession(session)
                .sender(request.getSender())
                .content(request.getContent())
                .context(request.getContext())
                .build();

        ChatMessage savedMessage = messageRepository.save(message);
        log.info("Added message with ID: {} to session: {}", savedMessage.getId(), request.getSessionId());

        return messageMapper.toDto(savedMessage);
    }
}
