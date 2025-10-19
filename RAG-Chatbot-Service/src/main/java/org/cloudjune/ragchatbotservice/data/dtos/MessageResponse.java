package org.cloudjune.ragchatbotservice.data.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.cloudjune.ragchatbotservice.data.enums.MessageSender;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    
    private Long id;
    private Long sessionId;
    private MessageSender sender;
    private String content;
    private String context;
    private LocalDateTime createdAt;
}
