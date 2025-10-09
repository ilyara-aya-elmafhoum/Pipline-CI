package com.wesports.backend.infrastructure.web.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Minimal OpenAPI/Swagger configuration for WeSports Backend API
 * 
 * This configuration is part of the Infrastructure layer in our Hexagonal Architecture,
 * providing basic API documentation without affecting the Domain or Application layers.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI wesportsOpenAPI() {
        return new OpenAPI()
                .openapi("3.0.1")  // Explicitly set OpenAPI version
                .info(new Info()
                        .title("WeSports Backend API")
                        .description("RESTful API for WeSports platform")
                        .version("1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Bearer Authentication", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .bearerFormat("JWT")
                                .scheme("bearer")
                                .description("JWT Authentication")));
    }
}
