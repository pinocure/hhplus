package com.hhplus.ecommerce.balance;

import com.hhplus.ecommerce.balance.application.port.in.BalanceUseCase;
import com.hhplus.ecommerce.balance.application.port.out.BalanceRepository;
import com.hhplus.ecommerce.balance.domain.Balance;
import com.hhplus.ecommerce.common.lock.DistributedLock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

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

        assertEquals(threadCount, successCount.get());
        assertEquals(0, failCount.get());
    }

    @Test
    @DisplayName("분산락 타임아웃 테스트")
    void testDistributedLockTimeout() throws InterruptedException {
        Long userId = 3L;
        Balance balance = new Balance(userId, new BigDecimal("1000"));
        balanceRepository.save(balance);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(2);
        AtomicInteger timeoutCount = new AtomicInteger(0);
        AtomicInteger successCount = new AtomicInteger(0);

        // Thread1: DB 비관적 락으로 오래 보유
        Thread thread1 = new Thread(() -> {
            try {
                startLatch.await();
                // 비관적 락을 사용하여 실제로 락을 오래 보유
                balanceRepository.findByUserIdWithLock(userId); // 비관적 락 획득
                Thread.sleep(1000); // DB 락 보유

                balanceUseCase.chargeBalance(userId, new BigDecimal("100"));
                successCount.incrementAndGet();
                System.out.println("Thread1 성공");
            } catch (Exception e) {
                System.err.println("Thread1 실패: " + e.getMessage());
            } finally {
                endLatch.countDown();
            }
        });

        // Thread2: 분산락 타임아웃 시도
        Thread thread2 = new Thread(() -> {
            try {
                startLatch.await();
                Thread.sleep(50); // Thread1이 먼저 시작하도록

                balanceUseCase.chargeBalance(userId, new BigDecimal("200"));
                successCount.incrementAndGet();
                System.out.println("Thread2 성공");
            } catch (Exception e) {
                System.err.println("Thread2 실패: " + e.getMessage());
                if (e.getMessage() != null && e.getMessage().contains("잠시 후 다시 시도")) {
                    timeoutCount.incrementAndGet();
                }
            } finally {
                endLatch.countDown();
            }
        });

        thread1.start();
        thread2.start();
        startLatch.countDown();

        endLatch.await(15, TimeUnit.SECONDS); // 타임아웃 방지

        System.out.println("성공 횟수: " + successCount.get());
        System.out.println("타임아웃 횟수: " + timeoutCount.get());
    }

}












