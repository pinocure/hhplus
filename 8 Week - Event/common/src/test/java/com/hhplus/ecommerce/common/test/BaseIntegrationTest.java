package com.hhplus.ecommerce.common.test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class BaseIntegrationTest {

    protected static final String MYSQL_VERSION = "mysql:8.0";
    protected static final String REDIS_VERSION = "redis:7-alpine";

    @Container
    protected static MySQLContainer<?> mysql = new MySQLContainer<>(MYSQL_VERSION)
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withCommand("--character-set-server=utf8mb4",
                    "--collation-server=utf8mb4_unicode_ci",
                    "--max_connections=200",
                    "--wait_timeout=300")
            .withReuse(true);

    @Container
    protected static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse(REDIS_VERSION))
            .withExposedPorts(6379)
            .withReuse(true);

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        // MySQL 설정
        registry.add("spring.datasource.url", () -> mysql.getJdbcUrl() + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");

        // HikariCP 설정 (테스트용으로 짧게)
        registry.add("spring.datasource.hikari.max-lifetime", () -> "30000");
        registry.add("spring.datasource.hikari.connection-timeout", () -> "3000");
        registry.add("spring.datasource.hikari.validation-timeout", () -> "2000");
        registry.add("spring.datasource.hikari.maximum-pool-size", () -> "5");
        registry.add("spring.datasource.hikari.minimum-idle", () -> "1");

        // Redis 설정
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
        registry.add("spring.data.redis.timeout", () -> "2000");

        // JPA 설정
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.MySQL8Dialect");
    }

    @BeforeAll
    static void beforeAll() {
        if (!mysql.isRunning()) {
            mysql.start();
        }
        if (!redis.isRunning()) {
            redis.start();
        }
    }

    @AfterAll
    static void afterAll() {
        // 테스트 종료 시 자동으로 정리하지 않음
    }

}



















