-- Balance Service 초기 데이터
USE balance_db;

CREATE TABLE IF NOT EXISTS balance (
    user_id BIGINT PRIMARY KEY,
    amount DECIMAL(19,2) NOT NULL
);

-- 5가지 잔액 패턴으로 500개 데이터 생성
DELIMITER $$
CREATE PROCEDURE insert_balance_data()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE balance_amount DECIMAL(19,2);

    WHILE i <= 500 DO
        CASE (i MOD 5)
            WHEN 0 THEN SET balance_amount = 0.00;        -- 잔액 없음
            WHEN 1 THEN SET balance_amount = 10000.00;    -- 1만원
            WHEN 2 THEN SET balance_amount = 50000.00;    -- 5만원
            WHEN 3 THEN SET balance_amount = 100000.00;   -- 10만원
            WHEN 4 THEN SET balance_amount = 500000.00;   -- 50만원
        END CASE;

        INSERT INTO balance (user_id, amount) VALUES (i, balance_amount);
        SET i = i + 1;
    END WHILE;
END$$
DELIMITER ;

CALL insert_balance_data();
DROP PROCEDURE insert_balance_data;












