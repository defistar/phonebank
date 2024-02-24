package com.store.phonebank;

import com.store.phonebank.services.seed.PhoneOnboardingSeeder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

@TestConfiguration
public class TestConfig {

    private static final Logger logger = LoggerFactory.getLogger(PhoneOnboardingSeeder.class);

    @Container
    public static PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("phonebank")
            .withUsername("admin")
            .withPassword("password")
            .withInitScript("phone-bank.sql");

    static {
        postgresqlContainer.start();
    }

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        String url = "r2dbc:postgresql://" + postgresqlContainer.getHost() + ":" + postgresqlContainer.getFirstMappedPort() + "/" + postgresqlContainer.getDatabaseName();
        registry.add("spring.r2dbc.url", () -> url);
        registry.add("spring.r2dbc.username", postgresqlContainer::getUsername);
        registry.add("spring.r2dbc.password", postgresqlContainer::getPassword);
    }
}
