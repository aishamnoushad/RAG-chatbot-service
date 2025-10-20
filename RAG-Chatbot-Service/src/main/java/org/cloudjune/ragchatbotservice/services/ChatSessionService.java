package org.cloudjune.ragchatbotservice.services;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cloudjune.ragchatbotservice.data.dtos.CreateSessionRequest;
import org.cloudjune.ragchatbotservice.data.dtos.SessionResponse;
import org.cloudjune.ragchatbotservice.data.entities.ChatSession;
import org.cloudjune.ragchatbotservice.data.mapper.ChatSessionMapper;
import org.cloudjune.ragchatbotservice.data.repositories.ChatSessionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChatSessionService {

    private final ChatSessionRepository sessionRepository;
    private final ChatSessionMapper sessionMapper;

    public SessionResponse createSession(CreateSessionRequest request) {
        log.info("Creating new chat session for user: {}", request.getUserId());

        ChatSession session = ChatSession.builder()
                .userId(request.getUserId())
                .title(request.getTitle())
                .isFavorite(false)
                .build();

        ChatSession savedSession = sessionRepository.save(session);
        log.info("Created chat session with ID: {}", savedSession.getId());

        return sessionMapper.toDto(savedSession);
    }

    @Transactional(readOnly = true)
    public List<SessionResponse> getUserSessions(String userId) {
        log.info("Retrieving sessions for user: {}", userId);

        List<ChatSession> sessions = sessionRepository.findByUserIdOrderByUpdatedAtDesc(userId);
        return sessions.stream()
                .map(sessionMapper::toDto)
                .collect(Collectors.toList());
    }
}
