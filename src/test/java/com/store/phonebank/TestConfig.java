package com.store.phonebank;

import com.store.phonebank.services.seed.DeviceInfoSeeder;
import com.store.phonebank.services.seed.PhoneOnboardingSeeder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

    @Configuration
    @Testcontainers
    public class TestConfig {

        @Container
        public static PostgreSQLContainer container = new PostgreSQLContainer("postgres:13.1")
                .withDatabaseName("integration-tests-db")
                .withUsername("admin")
                .withPassword("password");

        @DynamicPropertySource
        static void registerPgProperties(DynamicPropertyRegistry registry) {
            registry.add("spring.r2dbc.url", () -> "r2dbc:postgresql://" + container.getHost() + ":" + container.getFirstMappedPort() + "/" + container.getDatabaseName());
            registry.add("spring.r2dbc.username", container::getUsername);
            registry.add("spring.r2dbc.password", container::getPassword);
        }

        @Bean
        public CommandLineRunner seeder(DeviceInfoSeeder deviceInfoSeeder, PhoneOnboardingSeeder phoneOnboardingSeeder) {
            return args -> {
                deviceInfoSeeder.runSeed().block();
                phoneOnboardingSeeder.runSeed().block();
            };
        }

        @Bean
        public CommandLineRunner checkContainerStatus() {
            return args -> {
                if (container.isRunning()) {
                    System.out.println("Testcontainers database container is running.");
                } else {
                    System.out.println("Testcontainers database container is not running.");
                }
            };
        }
    }
