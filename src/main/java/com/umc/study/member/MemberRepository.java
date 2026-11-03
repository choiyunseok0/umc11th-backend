package com.umc.study.member;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepository {
    private final JdbcTemplate jdbc;

    public long insert(String email, String nickname, String passwordHash) {
        var key = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            var statement = connection.prepareStatement(
                "INSERT INTO member (email, nickname, password_hash) VALUES (?, ?, ?)",
                new String[]{"member_id"});
            statement.setString(1, email);
            statement.setString(2, nickname);
            statement.setString(3, passwordHash);
            return statement;
        }, key);
        return java.util.Objects.requireNonNull(key.getKey()).longValue();
    }
}
