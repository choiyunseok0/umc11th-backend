package com.umc.study.member;

import com.umc.study.auth.AuthInterceptor;
import com.umc.study.auth.JwtUtil;
import com.umc.study.common.ApiExceptionHandler;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MemberInfoTest {
    @Test
    void meRequiresVerifiedUnexpiredToken() throws Exception {
        String secret = "test-only-signing-key-0123456789abcdef";
        var jwt = new JwtUtil(secret, 3600);
        var repo = mock(MemberRepository.class);
        when(repo.findInfoById(7L)).thenReturn(Optional.of(
            new MemberInfoResponse("tester@example.com", "테스터", LocalDateTime.of(2026, 11, 10, 0, 0))));
        var mvc = MockMvcBuilders.standaloneSetup(new MemberController(new MemberService(repo)))
            .addMappedInterceptors(new String[]{"/members/me"}, new AuthInterceptor(jwt))
            .setControllerAdvice(new ApiExceptionHandler()).build();

        mvc.perform(get("/members/me")).andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("AUTH401_2"));

        String token = jwt.createAccessToken(7L, "tester@example.com");
        int index = token.indexOf('.') + 1;
        String tampered = token.substring(0, index) + (token.charAt(index) == 'a' ? 'b' : 'a') + token.substring(index + 1);
        mvc.perform(get("/members/me").header("Authorization", "Bearer " + tampered))
            .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.code").value("AUTH401_3"));

        String expired = Jwts.builder().subject("7").expiration(Date.from(Instant.now().minusSeconds(120)))
            .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8))).compact();
        mvc.perform(get("/members/me").header("Authorization", "Bearer " + expired))
            .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.code").value("AUTH401_4"));
        verifyNoInteractions(repo);

        mvc.perform(get("/members/me").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk()).andExpect(jsonPath("$.result.email").value("tester@example.com"))
            .andExpect(jsonPath("$.result.createdAt").exists())
            .andExpect(jsonPath("$.result.passwordHash").doesNotExist());
        verify(repo).findInfoById(7L);
    }
}
