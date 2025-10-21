package org.cloudjune.ragchatbotservice.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cloudjune.ragchatbotservice.security.service.ApiKeyService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {
    
    private final ApiKeyService apiKeyService;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final String[] excludedPatterns = {
            "/actuator/health",
            "/actuator/info",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**",
            "/configuration/ui",
            "/configuration/security",
            "/v3/api-docs",
            "/v3/api-docs/swagger-config",
            "/health/db"
    };

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        return Arrays.stream(excludedPatterns)
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String apiKey = request.getHeader("X-API-Key");
        
        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.warn("Missing API key in request");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"API key is required\"}");
            return;
        }
        
        if (!apiKeyService.isValidApiKey(apiKey)) {
            log.warn("Invalid API key provided: {}", apiKey);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Invalid API key\"}");
            return;
        }
        
        // Set authentication in security context
        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken(
                "api-user", 
                null, 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_API_USER"))
            );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.debug("API key authentication successful");
        
        filterChain.doFilter(request, response);
    }
}
