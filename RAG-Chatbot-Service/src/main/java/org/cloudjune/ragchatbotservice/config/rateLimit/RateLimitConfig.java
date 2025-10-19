package org.cloudjune.ragchatbotservice.config.rateLimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@Slf4j
public class RateLimitConfig {
    
    @Value("${app.rate-limit.requests-per-minute:60}")
    private int requestsPerMinute;
    
    @Bean
    public Bucket bucket() {
        log.info("Configuring rate limiting: {} requests per minute", requestsPerMinute);
        
        Bandwidth limit = Bandwidth.classic(requestsPerMinute, Refill.intervally(requestsPerMinute, Duration.ofMinutes(1)));
        return Bucket4j.builder()
                .addLimit(limit)
                .build();
    }
}
