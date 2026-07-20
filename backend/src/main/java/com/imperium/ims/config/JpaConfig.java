package com.imperium.ims.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * JPA configuration.
 *
 * <p>Provides the {@link AuditorAware} bean referenced by
 * {@code @EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")} in
 * {@link com.imperium.ims.ImperiumApplication}.
 *
 * <p>The current username is resolved from the Spring Security context so
 * that {@code created_by} / {@code updated_by} are automatically populated.
 */
@Configuration
public class JpaConfig {

    @Bean
    public AuditorAware<String> auditorAwareImpl() {
        return () -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return Optional.of("SYSTEM");
            }
            return Optional.of(auth.getName());
        };
    }
}
