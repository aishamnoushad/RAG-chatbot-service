package org.cloudjune.ragchatbotservice.security.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ApiKeyService {
    
    @Value("${app.api.key}")
    private String validApiKey;
    
    public boolean isValidApiKey(String apiKey) {
        boolean isValid = validApiKey != null && validApiKey.equals(apiKey);
        if (!isValid) {
            log.warn("API key validation failed for key: {}", apiKey);
        }
        return isValid;
    }
}
