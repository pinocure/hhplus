-- Coupon Service 초기 데이터
USE coupon_db;

CREATE TABLE IF NOT EXISTS coupon_event (
    id BIGINT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    discount_amount DECIMAL(19,2) NOT NULL,
    total_quantity INT NOT NULL,
    remaining_quantity INT NOT NULL,
    expires_at DATETIME NOT NULL,
    version BIGINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS coupon (
    code VARCHAR(50) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    coupon_event_id BIGINT NOT NULL,
    discount_amount DECIMAL(19,2) NOT NULL,
    issued_at DATETIME NOT NULL,
    expires_at DATETIME NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE
);

-- 쿠폰 이벤트 데이터 (5가지 타입)
INSERT INTO coupon_event (id, name, discount_amount, total_quantity, remaining_quantity, expires_at, version) VALUES
                          (1, '신규가입 쿠폰', 5000.00, 1000, 500, DATE_ADD(NOW(), INTERVAL 30 DAY), 0),
                          (2, '생일 쿠폰', 10000.00, 500, 300, DATE_ADD(NOW(), INTERVAL 7 DAY), 0),
                          (3, '시즌 할인 쿠폰', 3000.00, 2000, 1500, DATE_ADD(NOW(), INTERVAL 60 DAY), 0),
                          (4, 'VIP 전용 쿠폰', 20000.00, 100, 50, DATE_ADD(NOW(), INTERVAL 90 DAY), 0),
                          (5, '이벤트 쿠폰', 1000.00, 5000, 4000, DATE_ADD(NOW(), INTERVAL 14 DAY), 0);

-- 쿠폰 데이터 생성 (250개)
DELIMITER $$
CREATE PROCEDURE insert_coupon_data()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE event_id BIGINT;
    DECLARE disc_amount DECIMAL(19,2);
    DECLARE exp_date DATETIME;

    WHILE i <= 250 DO
        SET event_id = ((i - 1) MOD 5) + 1;

        SELECT discount_amount, expires_at INTO disc_amount, exp_date
        FROM coupon_event WHERE id = event_id;

        INSERT INTO coupon (code, user_id, coupon_event_id, discount_amount, issued_at, expires_at, used)
        VALUES (
           CONCAT('COUPON', LPAD(i, 6, '0')),
           ((i - 1) MOD 200) + 1,  -- user_id 1~200 반복
            event_id,
            disc_amount,
            DATE_SUB(NOW(), INTERVAL (i MOD 30) DAY),
            exp_date,
            (i MOD 10) = 0  -- 10%는 사용됨
        );
        SET i = i + 1;
    END WHILE;
END$$
DELIMITER ;

CALL insert_coupon_data();
DROP PROCEDURE insert_coupon_data;













