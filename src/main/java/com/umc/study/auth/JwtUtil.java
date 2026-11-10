package com.umc.study.auth;

import com.umc.study.common.ApiException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.http.HttpStatus;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    private final SecretKey key;
    private final long expirationSeconds;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.access-expiration}") long expirationSeconds) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        if (expirationSeconds <= 0) {
            throw new IllegalArgumentException("JWT access expiration must be positive");
        }
        this.expirationSeconds = expirationSeconds;
    }

    public String createAccessToken(long memberId, String email) {
        Instant now = Instant.now();
        return Jwts.builder()
            .subject(Long.toString(memberId))
            .claim("email", email)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusSeconds(expirationSeconds)))
            .signWith(key)
            .compact();
    }

    public long getMemberId(String token) {
        try {
            String subject = Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload().getSubject();
            long memberId = Long.parseLong(subject);
            if (memberId <= 0) throw new IllegalArgumentException("Invalid member ID");
            return memberId;
        } catch (ExpiredJwtException error) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "AUTH401_4",
                "토큰이 만료되었습니다. 다시 로그인해주세요.");
        } catch (JwtException | IllegalArgumentException error) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "AUTH401_3", "유효하지 않은 토큰입니다.");
        }
    }
}
