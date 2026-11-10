package com.umc.study.member;

import java.util.Optional;
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

    public Optional<LoginCredentials> findCredentialsByEmail(String email) {
        return jdbc.query(
            "SELECT member_id, email, nickname, password_hash FROM member WHERE email = ?",
            (rs, row) -> new LoginCredentials(rs.getLong("member_id"), rs.getString("email"),
                rs.getString("nickname"), rs.getString("password_hash")), email
        ).stream().findFirst();
    }

    public record LoginCredentials(long memberId, String email, String nickname, String passwordHash) {}

    public Optional<MemberInfoResponse> findInfoById(long memberId) {
        return jdbc.query(
            "SELECT email, nickname, created_at FROM member WHERE member_id = ?",
            (rs, row) -> new MemberInfoResponse(rs.getString("email"), rs.getString("nickname"),
                rs.getObject("created_at", java.time.LocalDateTime.class)), memberId
        ).stream().findFirst();
    }
}
