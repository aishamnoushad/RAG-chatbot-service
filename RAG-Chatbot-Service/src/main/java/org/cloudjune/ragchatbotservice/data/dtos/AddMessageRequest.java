package org.cloudjune.ragchatbotservice.data.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.cloudjune.ragchatbotservice.data.enums.MessageSender;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddMessageRequest {
    
    @NotNull(message = "Session ID is required")
    private Long sessionId;
    
    @NotNull(message = "Sender is required")
    private MessageSender sender;
    
    @NotBlank(message = "Content is required")
    private String content;
    
    private String context;
}
