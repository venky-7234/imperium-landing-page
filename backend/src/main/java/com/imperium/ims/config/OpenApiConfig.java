package com.imperium.ims.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 3 / Swagger UI configuration.
 *
 * <p>Exposes the API documentation at {@code /api/swagger-ui.html} with
 * a pre-configured Bearer JWT security scheme so developers can authenticate
 * and test endpoints directly from the Swagger UI.
 */
@Configuration
public class OpenApiConfig {

    @Value("${app.version:1.0.0-SNAPSHOT}")
    private String appVersion;

    private static final String SECURITY_SCHEME_NAME = "BearerAuth";

    @Bean
    public OpenAPI imperiumOpenAPI() {
        return new OpenAPI()
                .info(buildInfo())
                .servers(buildServers())
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, buildSecurityScheme()));
    }

    private Info buildInfo() {
        return new Info()
                .title("Imperium Invitation Management System API")
                .description("Production-ready REST API for managing invitations, events, users, and notifications.")
                .version(appVersion)
                .contact(new Contact()
                        .name("Imperium Engineering")
                        .email("engineering@imperium.com")
                        .url("https://imperium.com"))
                .license(new License()
                        .name("Proprietary")
                        .url("https://imperium.com/license"));
    }

    private List<Server> buildServers() {
        return List.of(
                new Server().url("/api").description("Current environment"),
                new Server().url("https://api.imperium.com").description("Production")
        );
    }

    private SecurityScheme buildSecurityScheme() {
        return new SecurityScheme()
                .name(SECURITY_SCHEME_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Provide a valid JWT access token obtained from /api/auth/login");
    }
}
