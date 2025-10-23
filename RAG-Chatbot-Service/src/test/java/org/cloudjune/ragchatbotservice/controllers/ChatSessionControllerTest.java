package org.cloudjune.ragchatbotservice.controllers;


import org.cloudjune.ragchatbotservice.controller.ChatSessionController;
import org.cloudjune.ragchatbotservice.data.dtos.*;
import org.cloudjune.ragchatbotservice.services.ChatSessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChatSessionControllerTest {

    private ChatSessionService sessionService;
    private ChatSessionController chatSessionController;

    private final String TEST_USER_ID = "user-123";
    private final Long TEST_SESSION_ID = 1L;

    @BeforeEach
    void setUp() {
        sessionService = mock(ChatSessionService.class);
        chatSessionController = new ChatSessionController(sessionService);
    }

    private SessionResponse createMockSessionResponse() {
        return SessionResponse.builder()
                .id(TEST_SESSION_ID)
                .userId(TEST_USER_ID)
                .title("Test Session")
                .isFavorite(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private CreateSessionRequest createMockCreateRequest() {
        return CreateSessionRequest.builder()
                .userId(TEST_USER_ID)
                .title("New Chat Session")
                .build();
    }

    private UpdateSessionRequest createMockUpdateRequest() {
        return UpdateSessionRequest.builder()
                .title("Updated Session Title")
                .isFavorite(true)
                .build();
    }

    @Test
    void createSession_ShouldReturnCreatedStatusAndSession() {
        // Given
        CreateSessionRequest request = createMockCreateRequest();
        SessionResponse expectedResponse = createMockSessionResponse();
        when(sessionService.createSession(request)).thenReturn(expectedResponse);

        // When
        ResponseEntity<SessionResponse> response = chatSessionController.createSession(request);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedResponse, response.getBody());
        verify(sessionService).createSession(request);
    }

    @Test
    void getUserSessions_ShouldReturnListOfSessions() {
        // Given
        List<SessionResponse> expectedSessions = List.of(
                createMockSessionResponse(),
                SessionResponse.builder()
                        .id(2L)
                        .userId(TEST_USER_ID)
                        .title("Another Session")
                        .isFavorite(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        );
        when(sessionService.getUserSessions(TEST_USER_ID)).thenReturn(expectedSessions);

        // When
        ResponseEntity<List<SessionResponse>> response = chatSessionController.getUserSessions(TEST_USER_ID);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(expectedSessions, response.getBody());
        verify(sessionService).getUserSessions(TEST_USER_ID);
    }

    @Test
    void getUserSessions_ShouldReturnEmptyListWhenNoSessions() {
        // Given
        when(sessionService.getUserSessions(TEST_USER_ID)).thenReturn(List.of());

        // When
        ResponseEntity<List<SessionResponse>> response = chatSessionController.getUserSessions(TEST_USER_ID);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(sessionService).getUserSessions(TEST_USER_ID);
    }

    @Test
    void getUserSessionsPaginated_ShouldReturnPagedResponse() {
        // Given
        PagedResponse<SessionResponse> expectedPagedResponse = PagedResponse.<SessionResponse>builder()
                .content(List.of(createMockSessionResponse()))
                .page(0)
                .size(10)
                .totalElements(1L)
                .totalPages(1)
                .last(true)
                .build();
        when(sessionService.getUserSessions(TEST_USER_ID, 0, 10)).thenReturn(expectedPagedResponse);

        // When
        ResponseEntity<PagedResponse<SessionResponse>> response =
                chatSessionController.getUserSessionsPaginated(TEST_USER_ID, 0, 10);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedPagedResponse, response.getBody());
        assertEquals(0, response.getBody().getPage());
        assertEquals(10, response.getBody().getSize());
        verify(sessionService).getUserSessions(TEST_USER_ID, 0, 10);
    }

    @Test
    void getSession_ShouldReturnSessionWhenExists() {
        // Given
        SessionResponse expectedSession = createMockSessionResponse();
        when(sessionService.getSession(TEST_SESSION_ID, TEST_USER_ID)).thenReturn(expectedSession);

        // When
        ResponseEntity<SessionResponse> response = chatSessionController.getSession(TEST_SESSION_ID, TEST_USER_ID);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedSession, response.getBody());
        verify(sessionService).getSession(TEST_SESSION_ID, TEST_USER_ID);
    }

    @Test
    void getSession_ShouldWorkWithNullUserId() {
        // Given
        SessionResponse expectedSession = createMockSessionResponse();
        when(sessionService.getSession(TEST_SESSION_ID, null)).thenReturn(expectedSession);

        // When
        ResponseEntity<SessionResponse> response = chatSessionController.getSession(TEST_SESSION_ID, null);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedSession, response.getBody());
        verify(sessionService).getSession(TEST_SESSION_ID, null);
    }

    @Test
    void updateSession_ShouldUpdateAndReturnSession() {
        // Given
        UpdateSessionRequest updateRequest = createMockUpdateRequest();
        SessionResponse expectedResponse = createMockSessionResponse();
        expectedResponse.setTitle("Updated Session Title");
        expectedResponse.setIsFavorite(true);
        when(sessionService.updateSession(TEST_SESSION_ID, TEST_USER_ID, updateRequest)).thenReturn(expectedResponse);

        // When
        ResponseEntity<SessionResponse> response =
                chatSessionController.updateSession(TEST_SESSION_ID, TEST_USER_ID, updateRequest);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedResponse, response.getBody());
        assertEquals("Updated Session Title", response.getBody().getTitle());
        assertTrue(response.getBody().getIsFavorite());
        verify(sessionService).updateSession(TEST_SESSION_ID, TEST_USER_ID, updateRequest);
    }

    @Test
    void updateSession_ShouldWorkWithNullUserId() {
        // Given
        UpdateSessionRequest updateRequest = createMockUpdateRequest();
        SessionResponse expectedResponse = createMockSessionResponse();
        when(sessionService.updateSession(TEST_SESSION_ID, null, updateRequest)).thenReturn(expectedResponse);

        // When
        ResponseEntity<SessionResponse> response =
                chatSessionController.updateSession(TEST_SESSION_ID, null, updateRequest);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedResponse, response.getBody());
        verify(sessionService).updateSession(TEST_SESSION_ID, null, updateRequest);
    }

    @Test
    void deleteSession_ShouldReturnNoContent() {
        // When
        ResponseEntity<Void> response = chatSessionController.deleteSession(TEST_SESSION_ID, TEST_USER_ID);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(sessionService).deleteSession(TEST_SESSION_ID, TEST_USER_ID);
    }

    @Test
    void deleteSession_ShouldWorkWithNullUserId() {
        // When
        ResponseEntity<Void> response = chatSessionController.deleteSession(TEST_SESSION_ID, null);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(sessionService).deleteSession(TEST_SESSION_ID, null);
    }

    @Test
    void completeCrudFlow_ShouldWorkCorrectly() {
        // Create Session
        CreateSessionRequest createRequest = createMockCreateRequest();
        SessionResponse createdSession = createMockSessionResponse();
        when(sessionService.createSession(createRequest)).thenReturn(createdSession);

        ResponseEntity<SessionResponse> createResponse = chatSessionController.createSession(createRequest);
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());

        // Get User Sessions
        List<SessionResponse> userSessions = List.of(createdSession);
        when(sessionService.getUserSessions(TEST_USER_ID)).thenReturn(userSessions);

        ResponseEntity<List<SessionResponse>> getResponse = chatSessionController.getUserSessions(TEST_USER_ID);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertFalse(getResponse.getBody().isEmpty());

        // Get Specific Session
        when(sessionService.getSession(TEST_SESSION_ID, TEST_USER_ID)).thenReturn(createdSession);

        ResponseEntity<SessionResponse> getSessionResponse =
                chatSessionController.getSession(TEST_SESSION_ID, TEST_USER_ID);
        assertEquals(HttpStatus.OK, getSessionResponse.getStatusCode());
        assertNotNull(getSessionResponse.getBody());

        // Update Session
        UpdateSessionRequest updateRequest = createMockUpdateRequest();
        SessionResponse updatedSession = createMockSessionResponse();
        updatedSession.setTitle("Updated Title");
        when(sessionService.updateSession(TEST_SESSION_ID, TEST_USER_ID, updateRequest)).thenReturn(updatedSession);

        ResponseEntity<SessionResponse> updateResponse =
                chatSessionController.updateSession(TEST_SESSION_ID, TEST_USER_ID, updateRequest);
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertNotNull(updateResponse.getBody());

        // Delete Session
        ResponseEntity<Void> deleteResponse = chatSessionController.deleteSession(TEST_SESSION_ID, TEST_USER_ID);
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        // Verify all service interactions
        verify(sessionService).createSession(createRequest);
        verify(sessionService).getUserSessions(TEST_USER_ID);
        verify(sessionService).getSession(TEST_SESSION_ID, TEST_USER_ID);
        verify(sessionService).updateSession(TEST_SESSION_ID, TEST_USER_ID, updateRequest);
        verify(sessionService).deleteSession(TEST_SESSION_ID, TEST_USER_ID);
    }
}
