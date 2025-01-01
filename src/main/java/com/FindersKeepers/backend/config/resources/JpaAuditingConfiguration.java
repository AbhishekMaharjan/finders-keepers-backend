package com.FindersKeepers.backend.config.resources;

import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Map;
import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfiguration {

    @Bean
    public AuditorAware<Long> auditorProvider() {
        return new AuditorAware<Long>() {
            @Override
            public @NonNull Optional<Long> getCurrentAuditor() {
                if (SecurityContextHolder.getContext().getAuthentication() != null) {
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    if (!authentication.getPrincipal().equals("anonymousUser") && !authentication.getPrincipal().equals("openapi")) {
                        if (authentication instanceof JwtAuthenticationToken) {
                            Jwt jwt = (Jwt) authentication.getPrincipal();
                            Map<String, Object> map = jwt.getClaim("user");
                            return Optional.of(Long.valueOf(map.get("id").toString()));
                        }
                        return Optional.of(0L);
                    } else
                        return Optional.of(1L);
                } else {
                    return Optional.of(0L);
                }
            }
        };
    }
}
