package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)   // 테스트 결과 순서대로 확인을 위해 추가
public class PointControllerMockTest {

    @Mock
    private UserPointTable userPointTable;              // 가짜 UserPointTable

    @Mock
    private PointHistoryTable pointHistoryTable;        // 가짜 PointHistoryTable

    @InjectMocks
    private PointController pointController;            // Mock들이 주입된 실제 Controller


    // 5. [예외 상황] 잔액 부족 시 포인트 사용 실패
    @Test
    @Order(1)
    void usePointFailWhenInsufficientPoints() {
        // given
        long userId = 5L;
        long useAmount = 5000L;
        UserPoint currentPoint = new UserPoint(userId, 1000L, System.currentTimeMillis());

        // mock 설정
        when(userPointTable.selectById(userId)).thenReturn(currentPoint);

        // when & then
        assertThatThrownBy(() -> pointController.use(userId, useAmount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("잔액이 부족합니다.")
                .satisfies(exception -> {
                    System.out.println("예외 발생: " + exception.getMessage());
                    System.out.println("요청 금액: " + useAmount);
                });

        // validate
        verify(userPointTable, times(1)).selectById(userId);    // selectedById가 1번 호출되었는가?
        verify(userPointTable, never()).insertOrUpdate(anyLong(), anyLong());           // 실패하였는데 수정사항이 있는가?
    }

    // 6. [예외 상황] 최소 금액 미만 충전 시 실패
    @Test
    @Order(2)
    void chargePointFailWhenAmountBelowMinimum() {
        // given
        long userId = 6L;
        long invalidAmount = 0L;

        // when & then
        assertThatThrownBy(() -> pointController.charge(userId, invalidAmount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("충전 금액은 100원 이상이어야 합니다.")
                .satisfies(exception -> {
                    System.out.println("예외 발생: " + exception.getMessage());
                    System.out.println("요청 충전 금액: " + invalidAmount + "원");
                });

        // validate
        verify(userPointTable, never()).selectById(userId);
        verify(userPointTable, never()).insertOrUpdate(anyLong(), anyLong());
    }

    // 7. [예외 상황] 최소 금액 미만 사용 시 실패
    @Test
    @Order(3)
    void usePointFailWhenAmountBelowMinimum() {
        // given
        long userId = 7L;
        long invalidAmount = 0L;

        // when & then
        assertThatThrownBy(() -> pointController.use(userId, invalidAmount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용 금액은 100원 이상이어야 합니다.")
                .satisfies(exception -> {
                    System.out.println("예외 발생: " + exception.getMessage());
                    System.out.println("요청 사용 금액: " + invalidAmount);
                });

        // validate
        verify(userPointTable, never()).selectById(anyLong());
        verify(userPointTable, never()).insertOrUpdate(anyLong(), anyLong());
    }

    // 8. [예외 상황] 최대 잔고 초과 충전 시 실패
    @Test
    @Order(4)
    void chargePointFailWhenExceedsMaxBalance() {
        // given
        long userId = 8L;
        long chargeAmount = 1000L;
        UserPoint currentPoint = new UserPoint(userId, 1000000L, System.currentTimeMillis());

        // mock 설정
        when(userPointTable.selectById(userId)).thenReturn(currentPoint);

        // when & then
        assertThatThrownBy(() -> pointController.charge(userId, chargeAmount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("충전 후 잔고가 최대 한도(1000000원)를 초과합니다.")
                .satisfies(exception -> {
                    System.out.println("예외 발생: " + exception.getMessage());
                    System.out.println("현재 포인트: " + currentPoint.point());
                    System.out.println("요청 충전 금액: " + chargeAmount);
                    System.out.println("충전 후 예상 포인트: " + (currentPoint.point() + chargeAmount));
                });

        // validate
        verify(userPointTable, times(1)).selectById(userId);
        verify(userPointTable, never()).insertOrUpdate(anyLong(), anyLong());
    }

}












