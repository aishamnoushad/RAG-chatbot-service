package org.cloudjune.ragchatbotservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "RAG Chatbot Service APIs", 
                version = "v1.0.0",
                description = "REST APIs for RAG Chatbot Service with chat sessions and message management",
                contact = @Contact(name = "CloudJune Team"),
                license = @License(name = "MIT License")
        )
)
public class OpenAPIConfiguration {

    @Value("${app.api.key:your-secure-api-key-here}")
    private String apiKey;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("ApiKeyAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .name("X-Api-Key")
                                        .description("API Key for authentication. Use the value from app.api.key property.")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("ApiKeyAuth"));
    }


}

