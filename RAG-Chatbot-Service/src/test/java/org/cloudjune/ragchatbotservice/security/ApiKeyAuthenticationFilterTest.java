package org.cloudjune.ragchatbotservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.cloudjune.ragchatbotservice.security.filter.ApiKeyAuthenticationFilter;
import org.cloudjune.ragchatbotservice.security.service.ApiKeyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.PrintWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class ApiKeyAuthenticationFilterTest {

    private ApiKeyService apiKeyService;
    private ApiKeyAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        apiKeyService = mock(ApiKeyService.class);
        filter = new ApiKeyAuthenticationFilter(apiKeyService);
    }

    @Test
    void excludedPath_isNotFiltered() throws ServletException, IOException {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        given(req.getServletPath()).willReturn("/actuator/health");

        // shouldNotFilter is true -> doFilterInternal still may be called by container, but in OncePerRequestFilter path,
        // Spr. calls shouldNotFilter and bypasses doFilterInternal. Here we simulate by verifying chain called without checks.
        filter.doFilter(req, res, chain);

        verify(chain, times(1)).doFilter(req, res);
        verify(res, never()).setStatus(eq(HttpServletResponse.SC_UNAUTHORIZED));
    }

    @Test
    void missingApiKey_returns401() throws ServletException, IOException {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        PrintWriter writer = mock(PrintWriter.class);
        
        given(req.getServletPath()).willReturn("/api/messages");
        given(req.getHeader("X-API-Key")).willReturn(null);
        given(res.getWriter()).willReturn(writer);

        filter.doFilter(req, res, chain);

        verify(res).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(writer).write("{\"error\":\"API key is required\"}");
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    void invalidApiKey_returns401() throws ServletException, IOException {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        PrintWriter writer = mock(PrintWriter.class);
        
        given(req.getServletPath()).willReturn("/api/messages");
        given(req.getHeader("X-API-Key")).willReturn("bad-key");
        given(apiKeyService.isValidApiKey("bad-key")).willReturn(false);
        given(res.getWriter()).willReturn(writer);

        filter.doFilter(req, res, chain);

        verify(res).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(writer).write("{\"error\":\"Invalid API key\"}");
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    void validApiKey_allowsChain() throws ServletException, IOException {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        given(req.getServletPath()).willReturn("/api/messages");
        given(req.getHeader("X-API-Key")).willReturn("good-key");
        given(apiKeyService.isValidApiKey("good-key")).willReturn(true);

        filter.doFilter(req, res, chain);

        verify(chain).doFilter(req, res);
        // status not forced to 401
        verify(res, never()).setStatus(eq(HttpServletResponse.SC_UNAUTHORIZED));
    }
}


