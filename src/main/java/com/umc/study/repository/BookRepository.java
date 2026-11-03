package com.umc.study.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class BookRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> findAll() {
        return jdbcTemplate.queryForList("SELECT * FROM book ORDER BY book_id");
    }

    public List<Map<String, Object>> findByCategory(long categoryId) {
        return jdbcTemplate.queryForList(
            "SELECT * FROM book WHERE category_id = ? ORDER BY book_id", categoryId);
    }

    public void save(Map<String, Object> body) {
        jdbcTemplate.update(
            "INSERT INTO book (category_id, title, description, is_available) VALUES (?, ?, ?, TRUE)",
            body.get("categoryId"), body.get("title"), body.get("description"));
    }
}
