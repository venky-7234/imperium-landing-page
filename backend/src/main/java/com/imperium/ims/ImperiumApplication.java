package com.imperium.ims;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Entry point for the Imperium Invitation Management System backend.
 *
 * <p>Enables:
 * <ul>
 *   <li>{@link EnableJpaAuditing} – auto-populates createdAt/updatedAt audit fields</li>
 *   <li>{@link EnableCaching}     – activates Spring Cache abstraction</li>
 *   <li>{@link EnableAsync}       – allows @Async on service methods (e.g. email dispatch)</li>
 *   <li>{@link EnableScheduling}  – activates @Scheduled tasks (e.g. analytics rollups)</li>
 * </ul>
 */
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
@EnableCaching
@EnableAsync
@EnableScheduling
public class ImperiumApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImperiumApplication.class, args);
    }
}
