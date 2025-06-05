package com.ghost.product_microservice;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    private static final DockerImageName POSTGRES_IMAGE = DockerImageName.parse("postgres:latest");

    @Bean(destroyMethod = "close")
    @ServiceConnection
    @SuppressWarnings("resource")
    public PostgreSQLContainer<?> postgresContainer() {
        PostgreSQLContainer<?> container = new PostgreSQLContainer<>(POSTGRES_IMAGE)
                .withDatabaseName("testdb")
                .withUsername("testuser")
                .withPassword("testpass");
        return container;
    }
}