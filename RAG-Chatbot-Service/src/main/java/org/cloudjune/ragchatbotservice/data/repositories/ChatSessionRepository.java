package org.cloudjune.ragchatbotservice.data.repositories;


import org.cloudjune.ragchatbotservice.data.entities.ChatSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.lang.ScopedValue;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {


    List<ChatSession> findByUserIdOrderByUpdatedAtDesc(String userId);

    Page<ChatSession> findByUserIdOrderByUpdatedAtDesc(String userId, Pageable pageable);

    Optional<ChatSession> findByIdAndUserId(Long id, String userId);

