package org.cloudjune.ragchatbotservice.services;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cloudjune.ragchatbotservice.data.dtos.AddMessageRequest;
import org.cloudjune.ragchatbotservice.data.dtos.MessageResponse;
import org.cloudjune.ragchatbotservice.data.dtos.PagedResponse;
import org.cloudjune.ragchatbotservice.data.entities.ChatMessage;
import org.cloudjune.ragchatbotservice.data.entities.ChatSession;
import org.cloudjune.ragchatbotservice.data.mapper.ChatMessageMapper;
import org.cloudjune.ragchatbotservice.data.repositories.ChatMessageRepository;
import org.cloudjune.ragchatbotservice.data.repositories.ChatSessionRepository;
import org.cloudjune.ragchatbotservice.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

    @Transactional(readOnly = true)
    public List<MessageResponse> getSessionMessages(Long sessionId) {
        log.info("Retrieving messages for session: {}", sessionId);

        List<ChatMessage> messages = messageRepository.findByChatSessionIdOrderByCreatedAtAsc(sessionId);
        return messages.stream()
                .map(messageMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PagedResponse<MessageResponse> getSessionMessages(Long sessionId, int page, int size) {
        log.info("Retrieving paginated messages for session: {}, page: {}, size: {}", sessionId, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<ChatMessage> messagePage = messageRepository.findByChatSessionIdOrderByCreatedAtAsc(sessionId, pageable);

        List<MessageResponse> content = messagePage.getContent().stream()
                .map(messageMapper::toDto)
                .collect(Collectors.toList());

        return PagedResponse.<MessageResponse>builder()
                .content(content)
                .page(messagePage.getNumber())
                .size(messagePage.getSize())
                .totalElements(messagePage.getTotalElements())
                .totalPages(messagePage.getTotalPages())
                .first(messagePage.isFirst())
                .last(messagePage.isLast())
                .build();
    }
}
