package com.elyashevich.card_manager.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    private static final String APP_TITLE = "Card manager application";
    private static final String APP_DESCRIPTION = "This is a sample API documentation using Swagger for Card manager application";
    private static final String APP_VERSION = "1.0";

    @Value("${application.open-api.email}")
    private String email;

    @Value("${application.open-api.server}")
    private String serverUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .servers(
                List.of(
                    new Server().url(this.serverUrl)
                )
            )
            .info(
                new Info()
                    .title(APP_TITLE)
                    .description(APP_DESCRIPTION)
                    .version(APP_VERSION)
                    .contact(new Contact().email(this.email))
            )
            .components(new Components()
                .addSecuritySchemes("bearerAuth",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("Bearer")
                        .bearerFormat("JWT")));
    }
}