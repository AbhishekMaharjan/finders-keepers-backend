package com.FindersKeepers.backend.config.security.utils;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Finders Keepers Backend Application",
                        email = "amaharjan715@gmail.com"
                ),
                description = "OpenApi documentation for Finders Keepers",
                title = "OpenApi Specification - Finders Keepers",
                version = "1.0"
        ), security = {
        @SecurityRequirement(
                name = "bearerAuth"
        )
}
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenAiConfig {
}
