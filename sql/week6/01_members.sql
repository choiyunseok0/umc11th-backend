USE umc11th;

CREATE TABLE IF NOT EXISTS member (
    member_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(254) NOT NULL,
    nickname VARCHAR(12) NOT NULL,
    password_hash VARCHAR(60) NOT NULL,
    CONSTRAINT uk_member_email UNIQUE (email),
    CONSTRAINT uk_member_nickname UNIQUE (nickname)
) DEFAULT CHARACTER SET utf8mb4;
