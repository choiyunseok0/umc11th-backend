package com.umc.study.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RentalRepository {
    private final JdbcTemplate jdbcTemplate;

    public void save(long userId, long bookId) {
        jdbcTemplate.update(
            "INSERT INTO rental (user_id, book_id, rented_at, due_at) " +
            "VALUES (?, ?, NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY))", userId, bookId);
    }
}
