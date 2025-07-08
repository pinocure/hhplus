package io.hhplus.tdd.point;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;


@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)  // 테스트 결과 순서대로 확인을 위해 추가
public class PointControllerTest {

    @Autowired
    private PointController pointController;

    // 1. [4가지 기본 기능] 포인트 조회
    @Test
    @Order(1)
    void getUserPoint() {
        // given
        long userId = 1L;

        // when
        UserPoint result = pointController.point(userId);

        // then
        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals(0L, result.point());

        // validate
        System.out.println("result: " + result);
        System.out.println("조회된 userId: " + result.id());
        System.out.println("First Point: " + result.point());
    }

    // 2. [4가지 기본 기능] 포인트 충전
    @Test
    @Order(2)
    void chargeUserPoint() {
        // given
        long userId = 2L;
        long chargeAmount = 1000L;

        // when
        UserPoint result = pointController.charge(userId, chargeAmount);

        // then
        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals(chargeAmount, result.point());

        // validate
        System.out.println("result: " + result);
        System.out.println("충전한 userId: " + result.id());
        System.out.println("충전 금액: " + result.point());
    }

    // 3. [4가지 기본 기능] 포인트 사용
    @Test
    @Order(3)
    void useUserPoint() {
        // given
        long userId = 3L;
        long chargeAmount = 1000L;
        long useAmount = 500L;

        // when
        pointController.charge(userId, chargeAmount);
        UserPoint result = pointController.use(userId, useAmount);

        // then
        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals(chargeAmount - useAmount, result.point());

        // validate
        System.out.println("result: " + result);
        System.out.println("포인트 사용한 userId: " + result.id());
        System.out.println("충전 & 사용 후 잔액: " + result.point());
    }

    // 4. [4가지 기본 기능] 포인트 내역 조회
    @Test
    @Order(4)
    void getUserPointHistory() {
        // given
        long userId = 4L;
        long chargeAmount = 5000L;
        long useAmount = 1000L;

        // when
        pointController.charge(userId, chargeAmount);
        pointController.use(userId, useAmount);
        List<PointHistory> histories = pointController.history(userId);

        // then
        assertNotNull(histories);
        assertEquals(2, histories.size());

        PointHistory chargeHistory = histories.get(0);
        assertEquals(userId, chargeHistory.userId());
        assertEquals(chargeAmount, chargeHistory.amount());
        assertEquals(TransactionType.CHARGE, chargeHistory.type());

        PointHistory useHistory = histories.get(1);
        assertEquals(userId, useHistory.userId());
        assertEquals(useAmount, useHistory.amount());
        assertEquals(TransactionType.USE, useHistory.type());

        // validate
        System.out.println("history 총 개수: " + histories.size());
        System.out.println("충전 금액: " + chargeHistory.amount());
        System.out.println("사용 금액: " + useHistory.amount());
    }

}
























