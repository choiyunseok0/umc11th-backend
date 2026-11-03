package com.umc.study.rating;

import java.sql.Statement;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RatingRepository {
    private final JdbcTemplate jdbc;

    boolean memberExists(long memberId) {
        return Boolean.TRUE.equals(jdbc.queryForObject(
            "SELECT EXISTS(SELECT 1 FROM member WHERE member_id = ?)", Boolean.class, memberId));
    }

    long insert(long memberId, CreateRatingRequest request) {
        var key = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            var statement = connection.prepareStatement(
                "INSERT INTO rating (member_id, movie_id, score, comment) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, memberId);
            statement.setLong(2, request.movieId());
            statement.setInt(3, request.score());
            statement.setString(4, request.comment());
            return statement;
        }, key);
        return Objects.requireNonNull(key.getKey()).longValue();
    }

    Optional<RatingResponse> lockById(long ratingId) {
        return jdbc.query("SELECT rating_id, member_id, movie_id, score, comment FROM rating WHERE rating_id = ? FOR UPDATE",
            (rs, row) -> new RatingResponse(rs.getLong("rating_id"), rs.getLong("member_id"),
                rs.getLong("movie_id"), rs.getInt("score"), rs.getString("comment")), ratingId)
            .stream().findFirst();
    }

    void update(long ratingId, UpdateRatingRequest request) {
        jdbc.update("UPDATE rating SET score = ?, comment = ? WHERE rating_id = ?",
            request.score(), request.comment(), ratingId);
    }

    void delete(long ratingId) {
        jdbc.update("DELETE FROM rating WHERE rating_id = ?", ratingId);
    }
}
