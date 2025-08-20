package com.hhplus.ecommerce.balance;

import com.hhplus.ecommerce.balance.application.port.in.BalanceUseCase;
import com.hhplus.ecommerce.balance.application.port.out.BalanceRepository;
import com.hhplus.ecommerce.balance.domain.Balance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
public class DistributedLockIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("balance")
            .withUsername("ruang")
            .withPassword("ruang");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    }

    @Autowired
    private BalanceUseCase balanceUseCase;

    @Autowired
    private BalanceRepository balanceRepository;

    @BeforeEach
    void setUp() {
        Balance balance = new Balance(1L, new BigDecimal("10000"));
        balanceRepository.save(balance);
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


        BigDecimal finalBalance = balanceUseCase.getBalance(userId);
        BigDecimal expectedBalance = new BigDecimal("10000").add(chargeAmount.multiply(new BigDecimal(threadCount)));

        assertEquals(threadCount, successCount.get());
        assertEquals(0, failCount.get());
        assertEquals(0, expectedBalance.compareTo(finalBalance));
    }


    @Test
    @DisplayName("분산락 타임아웃 테스트")
    void testDistributedLockTimeout() throws InterruptedException {
        Long userId = 3L;
        Balance balance = new Balance(userId, new BigDecimal("1000"));
        balanceRepository.save(balance);

        CountDownLatch latch = new CountDownLatch(2);
        AtomicInteger timeoutCount = new AtomicInteger(0);


        // 1번 Thread lock 오래 보유
        Thread thread1 = new Thread(() -> {
            try {
                balanceUseCase.chargeBalance(userId, new BigDecimal("100"));
                Thread.sleep(6000);
            } catch (Exception e) {
                // 무시
            } finally {
                latch.countDown();
            }
        });

        // 2번 Thread Timeout 발생
        Thread thread2 = new Thread(() -> {
            try {
                Thread.sleep(100);
                balanceUseCase.chargeBalance(userId, new BigDecimal("200"));
            } catch (Exception e) {
                if (e.getMessage().contains("잠시 후 다시 시도")) {
                    timeoutCount.incrementAndGet();
                }
            } finally {
                latch.countDown();
            }
        });


        thread1.start();
        thread2.start();
        latch.await();

        assertTrue(timeoutCount.get() > 0);
    }

}












