USE umc11th;

CREATE TABLE IF NOT EXISTS rating (
    rating_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    movie_id BIGINT NOT NULL,
    score INT NOT NULL,
    comment VARCHAR(500),
    CONSTRAINT fk_rating_member FOREIGN KEY (member_id) REFERENCES member(member_id),
    CONSTRAINT uk_rating_member_movie UNIQUE (member_id, movie_id),
    CONSTRAINT ck_rating_score CHECK (score BETWEEN 1 AND 5)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4;
