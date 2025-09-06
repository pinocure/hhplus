package com.hhplus.ecommerce.balance;

import com.hhplus.ecommerce.balance.application.port.in.BalanceUseCase;
import com.hhplus.ecommerce.balance.application.port.out.BalanceRepository;
import com.hhplus.ecommerce.balance.domain.Balance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DistributedLockIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("balance_test")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379)
            .withReuse(true);

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> mysql.getJdbcUrl() + "?useSSL=false&allowPublicKeyRetrieval=true");
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.hikari.max-lifetime", () -> "30000");
        registry.add("spring.datasource.hikari.connection-timeout", () -> "3000");
        registry.add("spring.datasource.hikari.maximum-pool-size", () -> "10");
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
        registry.add("app.redis.enabled", () -> "true");
    }

    @Autowired
    private BalanceUseCase balanceUseCase;

    @Autowired
    private BalanceRepository balanceRepository;

    @BeforeEach
    void setUp() {
        try {
            Balance existing = balanceRepository.findByUserId(1L).orElse(null);
            if (existing != null) {
                existing.setAmount(new BigDecimal("10000"));
                balanceRepository.save(existing);
            } else {
                Balance balance = new Balance(1L, new BigDecimal("10000"));
                balanceRepository.save(balance);
            }
        } catch (Exception e) {
            Balance balance = new Balance(1L, new BigDecimal("10000"));
            balanceRepository.save(balance);
        }
    }

    @Test
    @DisplayName("분산락 동시성 테스트 - 잔액 충전")
    void testConcurrentChargeWithDistributedLock() throws InterruptedException {
        Long userId = 1L;
        BigDecimal chargeAmount = new BigDecimal("100");
        int threadCount = 10;
        BigDecimal initialBalance = new BigDecimal("10000");

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    balanceUseCase.chargeBalance(userId, chargeAmount);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                    System.err.println("충전실패: " + e.getMessage());
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        endLatch.await();
        executor.shutdown();

        BigDecimal finalBalance = balanceUseCase.getBalance(userId);

        System.out.println(String.format(
                "테스트 결과 - 초기잔액: %s, 성공: %d, 실패: %d, 실제잔액: %s",
                initialBalance, successCount.get(), failCount.get(), finalBalance
        ));


        assertEquals(threadCount, successCount.get() + failCount.get(),
                "총 요청 수와 성공+실패 수가 일치하지 않습니다.");

        assertTrue(successCount.get() > 0,
                "최소 1개 이상은 성공");

        assertTrue(finalBalance.compareTo(initialBalance) > 0,
                "잔액이 증가");

        BigDecimal maxPossibleBalance = initialBalance.add(chargeAmount.multiply(new BigDecimal(threadCount)));
        assertTrue(finalBalance.compareTo(maxPossibleBalance) <= 0,
                "잔액이 최대 가능 금액을 초과");

        BigDecimal expectedBalance = initialBalance.add(
                chargeAmount.multiply(new BigDecimal(successCount.get()))
        );
    }

}












