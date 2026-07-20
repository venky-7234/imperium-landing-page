package com.imperium.ims.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Application-level beans and general configuration.
 *
 * <p>Defines the async task executor used by {@code @Async} service methods
 * (email dispatch, WhatsApp notifications, etc.).
 */
@Configuration
public class AppConfig {

    /**
     * Dedicated thread pool for async operations such as sending emails and
     * WhatsApp messages, preventing these I/O-bound tasks from consuming
     * the default Spring task executor.
     */
    @Bean(name = "asyncTaskExecutor")
    public Executor asyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("imperium-async-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }
}
