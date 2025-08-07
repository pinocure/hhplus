-- Order Service 초기 데이터
USE order_db;

CREATE TABLE IF NOT EXISTS orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    total_price DECIMAL(19,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    order_product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(19,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS order_coupon (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    coupon_code VARCHAR(50) NOT NULL,
    discount_amount DECIMAL(19,2) NOT NULL,
    used BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS order_product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    price DECIMAL(19,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS saga_state (
     id VARCHAR(255) PRIMARY KEY,
    order_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    current_step VARCHAR(100),
    completed_steps TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

-- 주문 상품 마스터 데이터 (50개)
DELIMITER $$
CREATE PROCEDURE insert_order_product_data()
BEGIN
    DECLARE i INT DEFAULT 1;
    WHILE i <= 50 DO
        INSERT INTO order_product (product_id, name, price) VALUES
        (i, CONCAT('상품_', i), 10000 + (i * 1000));
        SET i = i + 1;
    END WHILE;
END$$
DELIMITER ;

CALL insert_order_product_data();

-- 주문 데이터 생성 (50개)
DELIMITER $$
CREATE PROCEDURE insert_order_data()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE order_status VARCHAR(20);
    DECLARE total DECIMAL(19,2);

    WHILE i <= 50 DO
        -- 5가지 상태 패턴
        CASE (i MOD 5)
            WHEN 0 THEN SET order_status = 'PENDING';
            WHEN 1 THEN SET order_status = 'CONFIRMED';
            WHEN 2 THEN SET order_status = 'PAID';
            WHEN 3 THEN SET order_status = 'FAILED';
            WHEN 4 THEN SET order_status = 'PAID';
        END CASE;

        SET total = 10000 + (i * 500);

        INSERT INTO orders (user_id, total_price, status, created_at)
        VALUES (
           ((i - 1) MOD 50) + 1,  -- user_id 1~50 반복
            total,
            order_status,
            DATE_SUB(NOW(), INTERVAL (i MOD 30) DAY)
        );

        -- 각 주문당 1~3개의 주문 항목 생성
        INSERT INTO order_item (order_id, order_product_id, quantity, unit_price) VALUES
        (i, ((i - 1) MOD 100) + 1, 1 + (i MOD 3), 10000 + (i * 100));

        IF (i MOD 2) = 0 THEN
            INSERT INTO order_item (order_id, order_product_id, quantity, unit_price) VALUES
            (i, ((i + 10) MOD 100) + 1, 2, 15000);
        END IF;

        -- 30% 주문에 쿠폰 적용
        IF (i MOD 3) = 0 THEN
            INSERT INTO order_coupon (order_id, coupon_code, discount_amount, used) VALUES
            (i, CONCAT('USED', LPAD(i, 6, '0')), 5000.00, TRUE);
        END IF;

        SET i = i + 1;
        END WHILE;
END$$
DELIMITER ;

CALL insert_order_data();
DROP PROCEDURE insert_order_product_data;
DROP PROCEDURE insert_order_data;








