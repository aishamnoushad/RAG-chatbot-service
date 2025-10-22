package org.cloudjune.ragchatbotservice.services;

import jakarta.validation.Valid;
import org.cloudjune.ragchatbotservice.data.dtos.PagedResponse;
import org.cloudjune.ragchatbotservice.data.dtos.UpdateSessionRequest;
import org.cloudjune.ragchatbotservice.exception.BadRequestException;
import org.cloudjune.ragchatbotservice.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final ChatMessageService messageService;

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


    @Transactional(readOnly = true)
    public PagedResponse<SessionResponse> getUserSessions(String userId, int page, int size) {
        log.info("Retrieving paginated sessions for user: {}, page: {}, size: {}", userId, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<ChatSession> sessionPage = sessionRepository.findByUserIdOrderByUpdatedAtDesc(userId, pageable);

        List<SessionResponse> content = sessionPage.getContent().stream()
                .map(sessionMapper::toDto)
                .collect(Collectors.toList());

        return PagedResponse.<SessionResponse>builder()
                .content(content)
                .page(sessionPage.getNumber())
                .size(sessionPage.getSize())
                .totalElements(sessionPage.getTotalElements())
                .totalPages(sessionPage.getTotalPages())
                .first(sessionPage.isFirst())
                .last(sessionPage.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public SessionResponse getSession(Long sessionId, String userId) {
        ChatSession session = null;
        if(sessionId == null || userId == null){
            throw new BadRequestException("Session Id must be provided");
        }
        else if(userId == null || userId.isEmpty()){

            log.info("Retrieving session with id : {}", sessionId);
             session = sessionRepository.findById(sessionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Session not found with id: " + sessionId));
        }else{
            log.info("Retrieving session: {} for user: {}", sessionId, userId);


             session = sessionRepository.findByIdAndUserId(sessionId, userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Session not found with id: " + sessionId + " for user: " + userId));
        }


        return sessionMapper.toDto(session);
    }

    public SessionResponse updateSession(Long sessionId, String userId, UpdateSessionRequest request) {
        log.info("Updating session: {} for user: {}", sessionId, userId);

        ChatSession session = null;
        if(sessionId == null || userId == null){
            throw new BadRequestException("Session Id must be provided");
        }
        else if(userId == null || userId.isEmpty()){
            log.info("Retrieving session with id : {}", sessionId);
            session = sessionRepository.findById(sessionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Session not found with id: " + sessionId));
        }else{
            log.info("Retrieving session: {} for user: {}", sessionId, userId);

            session = sessionRepository.findByIdAndUserId(sessionId, userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Session not found with id: " + sessionId + " for user: " + userId));
        }


        if (request.getTitle() != null) {
            session.setTitle(request.getTitle());
        }

        if (request.getIsFavorite() != null) {
            session.setIsFavorite(request.getIsFavorite());
        }

        ChatSession updatedSession = sessionRepository.save(session);
        log.info("Updated session: {}", sessionId);

        return sessionMapper.toDto(updatedSession);
    }

    public void deleteSession(Long sessionId, String userId) {

        if(sessionId == null || userId == null){
            throw new BadRequestException("Session Id must be provided");
        }
        else if(userId == null || userId.isEmpty()){
            log.info("Deleting session: {} ", sessionId);

            if (!sessionRepository.existsById(sessionId)) {
                throw new ResourceNotFoundException("Session not found with id: " + sessionId);
            }
            messageService.deleteSessionMessages(sessionId);
            sessionRepository.deleteById(sessionId);
            log.info("Deleted session: {}", sessionId);

        }else{
            log.info("Deleting session: {} for user: {}", sessionId, userId);

            if (!sessionRepository.existsByIdAndUserId(sessionId, userId)) {
                throw new ResourceNotFoundException("Session not found with id: " + sessionId);
            }
            messageService.deleteSessionMessages(sessionId);
            sessionRepository.deleteByIdAndUserId(sessionId, userId);
            log.info("Deleted session: {}", sessionId);
        }



    }
}
