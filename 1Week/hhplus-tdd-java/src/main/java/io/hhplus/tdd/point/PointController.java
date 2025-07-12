package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/point")
public class PointController {

    private static final Logger log = LoggerFactory.getLogger(PointController.class);

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    public PointController(UserPointTable userPointTable, PointHistoryTable pointHistoryTable) {
        this.userPointTable = userPointTable;
        this.pointHistoryTable = pointHistoryTable;
    }

    // 최대 잔액
    private static final long MAX_BALANCE = 1000000L;

    // 1회 최대 충전 금액
    private static final long MAX_CHARGE_AMOUNT = 100000L;

    // 최소 거래 금액
    private static final long MIN_AMOUNT = 100L;


    // 특정 유저의 포인트를 조회하는 기능
    @GetMapping("{id}")
    public UserPoint point(
            @PathVariable long id
    ) {
        log.info("포인트 조회 요청 - 사용자 ID: {}", id);
        return userPointTable.selectById(id);
    }


    // 특정 유저의 포인트 충전/이용 내역을 조회하는 기능
    @GetMapping("{id}/histories")
    public List<PointHistory> history(
            @PathVariable long id
    ) {
        log.info("포인트 내역 조회 요청 - 사용자 ID: {}", id);
        return pointHistoryTable.selectAllByUserId(id);
    }


    // 특정 유저의 포인트를 충전하는 기능을 작성
    @PatchMapping("{id}/charge")
    public UserPoint charge(
            @PathVariable long id,
            @RequestBody long amount
    ) {
        log.info("포인트 충전 요청 - 사용자 ID: {}, 충전 금액: {}", id, amount);

        // [기능 추가 구현] 충전 금액 유효성 검사
        if (amount < MIN_AMOUNT) {
            throw new IllegalArgumentException("충전 금액은 " + MIN_AMOUNT + "원 이상이어야 합니다.");
        }

        if (amount > MAX_CHARGE_AMOUNT) {
            throw new IllegalArgumentException("한 번에 충전할 수 있는 최대 금액은 " + MAX_CHARGE_AMOUNT + "원 입니다.");
        }


        // 현재 포인트 확인
        UserPoint currentPoint = userPointTable.selectById(id);

        // 새로운 포인트 계산
        long newPoint = currentPoint.point() + amount;

        // [기능 추가 구현] 최대 잔고 초과 검사
        if (newPoint > MAX_BALANCE) {
            throw new IllegalArgumentException("충전 후 잔고가 최대 한도(" + MAX_BALANCE + "원)를 초과합니다.");
        }

        // 포인트 업데이트
        UserPoint updatePoint = userPointTable.insertOrUpdate(id, newPoint);

        // 포인트 기록
        pointHistoryTable.insert(id, amount, TransactionType.CHARGE, System.currentTimeMillis());

        return updatePoint;
    }


    // 특정 유저의 포인트를 사용하는 기능을 작성
    @PatchMapping("{id}/use")
    public UserPoint use(
            @PathVariable long id,
            @RequestBody long amount
    ) {
        log.info("포인트 사용 요청 - 사용자 ID: {}, 사용 금액: {}", id, amount);

        // [기능 추가 구현] 사용 금액 유효성 검사
        if (amount < MIN_AMOUNT) {
            throw new IllegalArgumentException("사용 금액은 " + MIN_AMOUNT + "원 이상이어야 합니다.");
        }

        // 현재 포인트 조회
        UserPoint currentPoint = userPointTable.selectById(id);

        // [기능 추가 구현] 잔액 확인
        if (currentPoint.point() < amount) {
            throw new IllegalArgumentException("잔액이 부족합니다. 현재 포인트: " + currentPoint.point());
        }

        // 새로운 포인트 계산
        long newPoint = currentPoint.point() - amount;

        // 포인트 업데이트
        UserPoint updatePoint = userPointTable.insertOrUpdate(id, newPoint);

        // 포인트 내역 기록
        pointHistoryTable.insert(id, amount, TransactionType.USE, System.currentTimeMillis());

        return updatePoint;
    }

}













