-- Product Service 초기 데이터
USE product_db;

CREATE TABLE IF NOT EXISTS product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    price DECIMAL(19,2) NOT NULL,
    stock INT NOT NULL,
    reserved_stock INT NOT NULL DEFAULT 0,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS popular_product_view (
    product_id BIGINT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    price DECIMAL(19,2) NOT NULL,
    stock INT NOT NULL,
    sales_count INT NOT NULL DEFAULT 0,
    last_updated DATETIME NOT NULL
);

-- 상품 데이터 (5가지 카테고리별 100개씩, 총 500개)
DELIMITER $$
CREATE PROCEDURE insert_product_data()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE prod_name VARCHAR(50);
    DECLARE prod_price DECIMAL(19,2);
    DECLARE prod_stock INT;

    WHILE i <= 500 DO
        CASE ((i - 1) DIV 100)
            WHEN 0 THEN  -- 전자제품
                SET prod_name = CONCAT('노트북_', LPAD((i MOD 100) + 1, 3, '0'));
                SET prod_price = 500000 + (i MOD 10) * 100000;
                SET prod_stock = 10 + (i MOD 20);
            WHEN 1 THEN  -- 의류
                SET prod_name = CONCAT('티셔츠_', LPAD((i MOD 100) + 1, 3, '0'));
                SET prod_price = 10000 + (i MOD 5) * 5000;
                SET prod_stock = 50 + (i MOD 50);
            WHEN 2 THEN  -- 도서
                SET prod_name = CONCAT('책_', LPAD((i MOD 100) + 1, 3, '0'));
                SET prod_price = 8000 + (i MOD 8) * 2000;
                SET prod_stock = 100 + (i MOD 100);
            WHEN 3 THEN  -- 식품
                SET prod_name = CONCAT('과자_', LPAD((i MOD 100) + 1, 3, '0'));
                SET prod_price = 1000 + (i MOD 10) * 500;
                SET prod_stock = 200 + (i MOD 100);
            WHEN 4 THEN  -- 화장품
                SET prod_name = CONCAT('화장품_', LPAD((i MOD 100) + 1, 3, '0'));
                SET prod_price = 20000 + (i MOD 10) * 5000;
                SET prod_stock = 30 + (i MOD 30);
        END CASE;

        INSERT INTO product (name, price, stock, reserved_stock, version)
        VALUES (prod_name, prod_price, prod_stock, 0, 0);

        -- 인기 상품 뷰에도 데이터 추가 (상위 100개만)
        IF i <= 100 THEN
            INSERT INTO popular_product_view (product_id, name, price, stock, sales_count, last_updated)
            VALUES (i, prod_name, prod_price, prod_stock, (1000 - i) * 10, NOW());
        END IF;

        SET i = i + 1;
    END WHILE;
END$$
DELIMITER ;

CALL insert_product_data();
DROP PROCEDURE insert_product_data;
















