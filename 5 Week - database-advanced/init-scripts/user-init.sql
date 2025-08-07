-- User Service 초기 데이터
USE user_db;

CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT
);

-- 사용자 데이터 생성 (100개)
DELIMITER $$
CREATE PROCEDURE insert_user_data()
BEGIN
    DECLARE i INT DEFAULT 1;

    WHILE i <= 100 DO
        INSERT INTO user (id) VALUES (i);
        SET i = i + 1;
    END WHILE;
END$$
DELIMITER ;

CALL insert_user_data();
DROP PROCEDURE insert_user_data;














