package org.cloudjune.ragchatbotservice.data.repositories;



import org.cloudjune.ragchatbotservice.data.entities.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {


    List<ChatMessage> findByChatSessionIdOrderByCreatedAtAsc(Long sessionId);

    Page<ChatMessage> findByChatSessionIdOrderByCreatedAtAsc(Long sessionId, Pageable pageable);

    @Query("SELECT m FROM ChatMessage m JOIN m.chatSession s WHERE s.id = :sessionId AND s.userId = :userId ORDER BY m.createdAt ASC")
    List<ChatMessage> findBySessionIdAndUserIdOrderByCreatedAtAsc(@Param("sessionId") Long sessionId,
                                                                  @Param("userId") String userId);

    @Query(
            value = """
        SELECT m FROM ChatMessage m
        JOIN m.chatSession s
        WHERE s.id = :sessionId AND s.userId = :userId
        ORDER BY m.createdAt ASC
        """,
            countQuery = """
        SELECT COUNT(m)
        FROM ChatMessage m
        JOIN m.chatSession s
        WHERE s.id = :sessionId AND s.userId = :userId
        """
    )
    Page<ChatMessage> findBySessionIdAndUserIdOrderByCreatedAtAsc(@Param("sessionId") Long sessionId,
                                                                  @Param("userId") String userId,
                                                                  Pageable pageable);

    void deleteByChatSessionId(Long sessionId);

    @Transactional
    @Modifying
    @Query("delete from ChatMessage cm where cm.chatSession.id = :sessionId and cm.chatSession.userId = :userId")
    void deleteByChatSessionIdAndUserId(@Param("sessionId") Long sessionId, @Param("userId") String userId);
}

