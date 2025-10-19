package org.cloudjune.ragchatbotservice.data.mapper;

import org.cloudjune.ragchatbotservice.data.dtos.SessionResponse;
import org.cloudjune.ragchatbotservice.data.entities.ChatMessage;
import org.cloudjune.ragchatbotservice.data.entities.ChatSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ChatMessageMapper.class})
public interface ChatSessionMapper {

    @Mapping(source = "messages", target = "messageCount", qualifiedByName = "countMessages")
    SessionResponse toDto(ChatSession chatSession);

    @Named("countMessages")
    default Integer countMessages(List<ChatMessage> messages) {
        return messages == null ? 0 : messages.size();
    }

    @Mapping(target = "messages", ignore = true) // Avoid circular reference, handle separately if needed
    ChatSession toEntity(SessionResponse sessionResponse);
}
