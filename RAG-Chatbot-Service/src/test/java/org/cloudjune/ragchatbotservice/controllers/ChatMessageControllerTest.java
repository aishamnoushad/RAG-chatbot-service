package org.cloudjune.ragchatbotservice.controllers;


import org.cloudjune.ragchatbotservice.controller.ChatMessageController;
import org.cloudjune.ragchatbotservice.data.dtos.AddMessageRequest;
import org.cloudjune.ragchatbotservice.data.dtos.MessageResponse;
import org.cloudjune.ragchatbotservice.data.dtos.PagedResponse;
import org.cloudjune.ragchatbotservice.data.enums.MessageSender;
import org.cloudjune.ragchatbotservice.services.ChatMessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ChatMessageControllerTest {

    private ChatMessageService messageService;
    private ChatMessageController chatMessageController;

    private final Long TEST_SESSION_ID = 1L;
    private final String TEST_USER_ID = "user-123";
    private final Long TEST_MESSAGE_ID = 1L;

    @BeforeEach
    void setUp() {
        messageService = mock(ChatMessageService.class);
        chatMessageController = new ChatMessageController(messageService);
    }

    private MessageResponse createMockMessageResponse() {
        return MessageResponse.builder()
                .id(TEST_MESSAGE_ID)
                .sessionId(TEST_SESSION_ID)
                .content("Test message content")
                .context("user")
                .sender(MessageSender.ASSISTANT)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private AddMessageRequest createMockAddMessageRequest() {
        return AddMessageRequest.builder()
                .sessionId(TEST_SESSION_ID)
                .content("Test message content")
                .context("user")
                .sender(MessageSender.ASSISTANT)
                .build();
    }

    @Test
    void addMessage_ShouldReturnCreatedStatusAndMessage() {
        // Given
        AddMessageRequest request = createMockAddMessageRequest();
        MessageResponse expectedResponse = createMockMessageResponse();
        when(messageService.addMessage(request)).thenReturn(expectedResponse);

        // When
        ResponseEntity<MessageResponse> response = chatMessageController.addMessage(request);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedResponse, response.getBody());
        verify(messageService).addMessage(request);
    }

    @Test
    void getSessionMessages_ShouldReturnListOfMessages() {
        // Given
        List<MessageResponse> expectedMessages = List.of(
                createMockMessageResponse(),
                MessageResponse.builder()
                        .id(2L)
                        .sessionId(TEST_SESSION_ID)
                        .content("Another message")
                        .sender(MessageSender.ASSISTANT)
                        .createdAt(LocalDateTime.now())
                        .build()
        );
        when(messageService.getSessionMessages(TEST_SESSION_ID)).thenReturn(expectedMessages);

        // When
        ResponseEntity<List<MessageResponse>> response = chatMessageController.getSessionMessages(TEST_SESSION_ID);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(expectedMessages, response.getBody());
        verify(messageService).getSessionMessages(TEST_SESSION_ID);
    }

    @Test
    void getSessionMessages_ShouldReturnEmptyListWhenNoMessages() {
        // Given
        when(messageService.getSessionMessages(TEST_SESSION_ID)).thenReturn(List.of());

        // When
        ResponseEntity<List<MessageResponse>> response = chatMessageController.getSessionMessages(TEST_SESSION_ID);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(messageService).getSessionMessages(TEST_SESSION_ID);
    }

    @Test
    void getSessionMessagesPaginated_ShouldReturnPagedResponse() {
        // Given
        List<MessageResponse> messageList = List.of(createMockMessageResponse());
        PagedResponse<MessageResponse> expectedPagedResponse = PagedResponse.<MessageResponse>builder()
                .content(messageList)
                .page(0)
                .size(20)
                .totalElements(1L)
                .totalPages(1)
                .last(true)
                .build();
        when(messageService.getSessionMessages(TEST_SESSION_ID, 0, 20)).thenReturn(expectedPagedResponse);

        // When
        ResponseEntity<PagedResponse<MessageResponse>> response =
                chatMessageController.getSessionMessagesPaginated(TEST_SESSION_ID, 0, 20);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedPagedResponse, response.getBody());
        assertEquals(0, response.getBody().getPage());
        assertEquals(20, response.getBody().getSize());
        assertEquals(1, response.getBody().getContent().size());
        verify(messageService).getSessionMessages(TEST_SESSION_ID, 0, 20);
    }

    @Test
    void getSessionMessagesWithUser_ShouldReturnMessages() {
        // Given
        List<MessageResponse> expectedMessages = List.of(createMockMessageResponse());
        when(messageService.getSessionMessages(TEST_SESSION_ID, TEST_USER_ID)).thenReturn(expectedMessages);

        // When
        ResponseEntity<List<MessageResponse>> response =
                chatMessageController.getSessionMessagesWithUser(TEST_SESSION_ID, TEST_USER_ID);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(expectedMessages, response.getBody());
        verify(messageService).getSessionMessages(TEST_SESSION_ID, TEST_USER_ID);
    }

    @Test
    void getSessionMessagesWithUser_ShouldReturnEmptyListWhenNoMessages() {
        // Given
        when(messageService.getSessionMessages(TEST_SESSION_ID, TEST_USER_ID)).thenReturn(List.of());

        // When
        ResponseEntity<List<MessageResponse>> response =
                chatMessageController.getSessionMessagesWithUser(TEST_SESSION_ID, TEST_USER_ID);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(messageService).getSessionMessages(TEST_SESSION_ID, TEST_USER_ID);
    }

    @Test
    void getSessionMessagesWithUserPaginated_ShouldReturnPagedResponse() {
        // Given
        List<MessageResponse> messageList = List.of(createMockMessageResponse());
        PagedResponse<MessageResponse> expectedPagedResponse = PagedResponse.<MessageResponse>builder()
                .content(messageList)
                .page(0)
                .size(20)
                .totalElements(1L)
                .totalPages(1)
                .last(true)
                .build();
        when(messageService.getSessionMessages(TEST_SESSION_ID, TEST_USER_ID, 0, 20)).thenReturn(expectedPagedResponse);

        // When
        ResponseEntity<PagedResponse<MessageResponse>> response =
                chatMessageController.getSessionMessagesWithUserPaginated(TEST_SESSION_ID, TEST_USER_ID, 0, 20);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedPagedResponse, response.getBody());
        assertEquals(0, response.getBody().getPage());
        assertEquals(20, response.getBody().getSize());
        assertEquals(1, response.getBody().getContent().size());
        verify(messageService).getSessionMessages(TEST_SESSION_ID, TEST_USER_ID, 0, 20);
    }

    @Test
    void deleteMessages_ShouldReturnNoContent() {
        // When
        ResponseEntity<Void> response = chatMessageController.deleteMessages(TEST_SESSION_ID, TEST_USER_ID);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(messageService).deleteMessage(TEST_SESSION_ID, TEST_USER_ID);
    }

    @Test
    void deleteMessages_ShouldWorkWithNullUserId() {
        // When
        ResponseEntity<Void> response = chatMessageController.deleteMessages(TEST_SESSION_ID, null);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(messageService).deleteMessage(TEST_SESSION_ID, null);
    }

    @Test
    void completeCrudFlow_ShouldWorkCorrectly() {
        // Add Message
        AddMessageRequest addRequest = createMockAddMessageRequest();
        MessageResponse addedMessage = createMockMessageResponse();
        when(messageService.addMessage(addRequest)).thenReturn(addedMessage);

        ResponseEntity<MessageResponse> addResponse = chatMessageController.addMessage(addRequest);
        assertEquals(HttpStatus.CREATED, addResponse.getStatusCode());
        assertNotNull(addResponse.getBody());

        // Get Session Messages
        List<MessageResponse> sessionMessages = List.of(addedMessage);
        when(messageService.getSessionMessages(TEST_SESSION_ID)).thenReturn(sessionMessages);

        ResponseEntity<List<MessageResponse>> getResponse = chatMessageController.getSessionMessages(TEST_SESSION_ID);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertFalse(getResponse.getBody().isEmpty());

        // Get Session Messages with User
        when(messageService.getSessionMessages(TEST_SESSION_ID, TEST_USER_ID)).thenReturn(sessionMessages);

        ResponseEntity<List<MessageResponse>> getUserResponse =
                chatMessageController.getSessionMessagesWithUser(TEST_SESSION_ID, TEST_USER_ID);
        assertEquals(HttpStatus.OK, getUserResponse.getStatusCode());
        assertNotNull(getUserResponse.getBody());

        // Get Paginated Messages
        PagedResponse<MessageResponse> pagedResponse = PagedResponse.<MessageResponse>builder()
                .content(sessionMessages)
                .page(0)
                .size(20)
                .totalElements(1L)
                .totalPages(1)
                .last(true)
                .build();
        when(messageService.getSessionMessages(TEST_SESSION_ID, 0, 20)).thenReturn(pagedResponse);

        ResponseEntity<PagedResponse<MessageResponse>> getPaginatedResponse =
                chatMessageController.getSessionMessagesPaginated(TEST_SESSION_ID, 0, 20);
        assertEquals(HttpStatus.OK, getPaginatedResponse.getStatusCode());
        assertNotNull(getPaginatedResponse.getBody());

        // Delete Messages
        ResponseEntity<Void> deleteResponse = chatMessageController.deleteMessages(TEST_SESSION_ID, TEST_USER_ID);
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        // Verify all service interactions
        verify(messageService).addMessage(addRequest);
        verify(messageService).getSessionMessages(TEST_SESSION_ID);
        verify(messageService).getSessionMessages(TEST_SESSION_ID, TEST_USER_ID);
        verify(messageService).getSessionMessages(TEST_SESSION_ID, 0, 20);
        verify(messageService).deleteMessage(TEST_SESSION_ID, TEST_USER_ID);
    }

    @Test
    void paginatedMethods_ShouldUseDefaultParameters() {
        // Test default pagination parameters for getSessionMessagesPaginated
        List<MessageResponse> messageList = List.of(createMockMessageResponse());
        PagedResponse<MessageResponse> expectedPagedResponse = PagedResponse.<MessageResponse>builder()
                .content(messageList)
                .page(0)
                .size(20)
                .totalElements(1L)
                .totalPages(1)
                .last(true)
                .build();

        when(messageService.getSessionMessages(TEST_SESSION_ID, 0, 20)).thenReturn(expectedPagedResponse);

        ResponseEntity<PagedResponse<MessageResponse>> response =
                chatMessageController.getSessionMessagesPaginated(TEST_SESSION_ID, 0, 20);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(messageService).getSessionMessages(TEST_SESSION_ID, 0, 20);
    }

    @Test
    void differentPaginationParameters_ShouldWorkCorrectly() {
        // Test different pagination parameters
        List<MessageResponse> messageList = List.of(createMockMessageResponse());
        PagedResponse<MessageResponse> expectedPagedResponse = PagedResponse.<MessageResponse>builder()
                .content(messageList)
                .page(2)
                .size(50)
                .totalElements(100L)
                .totalPages(2)
                .last(true)
                .build();

        when(messageService.getSessionMessages(TEST_SESSION_ID, 2, 50)).thenReturn(expectedPagedResponse);

        ResponseEntity<PagedResponse<MessageResponse>> response =
                chatMessageController.getSessionMessagesPaginated(TEST_SESSION_ID, 2, 50);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().getPage());
        assertEquals(50, response.getBody().getSize());
        verify(messageService).getSessionMessages(TEST_SESSION_ID, 2, 50);
    }
}
