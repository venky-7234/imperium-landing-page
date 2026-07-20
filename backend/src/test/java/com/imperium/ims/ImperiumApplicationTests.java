package com.imperium.ims;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Smoke test that verifies the Spring application context loads without errors.
 *
 * <p>This test does NOT require a running database — it uses H2 in-memory via
 * the {@code test} profile (override application.yml with test YAML when ready).
 */
@SpringBootTest
@ActiveProfiles("dev")
class ImperiumApplicationTests {

    @Test
    void contextLoads() {
        // If the application context fails to load, this test will fail automatically.
    }
}
