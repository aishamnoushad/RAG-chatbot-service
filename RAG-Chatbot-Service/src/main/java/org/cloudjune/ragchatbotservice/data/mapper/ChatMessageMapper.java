package org.cloudjune.ragchatbotservice.data.mapper;

import org.cloudjune.ragchatbotservice.data.dtos.MessageResponse;
import org.cloudjune.ragchatbotservice.data.entities.ChatMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper {

    @Mapping(source = "chatSession.id", target = "sessionId")
    MessageResponse toDto(ChatMessage chatMessage);

    @Mapping(source = "sessionId", target = "chatSession.id")
    ChatMessage toEntity(MessageResponse messageResponse);
}
