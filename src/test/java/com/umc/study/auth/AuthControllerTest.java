package com.umc.study.auth;

import com.umc.study.member.MemberRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {
    private static final String TEST_KEY = "test-only-signing-key-0123456789abcdef";
    private final MemberRepository repository = mock(MemberRepository.class);
    private MockMvc mvc;

    @BeforeEach
    void setup() {
        var hash = new BCryptPasswordEncoder().encode("Practice1!");
        when(repository.findCredentialsByEmail("tester@example.com")).thenReturn(
            Optional.of(new MemberRepository.LoginCredentials(7L, "tester@example.com", "테스터", hash)));
        mvc = MockMvcBuilders.standaloneSetup(new AuthController(
            new AuthService(repository, new JwtUtil(TEST_KEY, 3600))))
            .setControllerAdvice(new AuthExceptionHandler()).build();
    }

    @Test
    void validLoginReturnsSignedTokenForMember() throws Exception {
        var result = mvc.perform(login("TESTER@example.com", "Practice1!"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.nickname").value("테스터"))
            .andExpect(jsonPath("$.result.password").doesNotExist())
            .andReturn();
        String token = com.jayway.jsonpath.JsonPath.read(result.getResponse().getContentAsString(), "$.result.accessToken");
        var key = Keys.hmacShaKeyFor(TEST_KEY.getBytes(StandardCharsets.UTF_8));
        var claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        assertThat(claims.getSubject()).isEqualTo("7");
        assertThat(claims.get("email", String.class)).isEqualTo("tester@example.com");
        assertThat(claims.getExpiration()).isAfter(claims.getIssuedAt());
    }

    @Test
    void missingMemberAndWrongPasswordHaveSameResponse() throws Exception {
        var wrong = mvc.perform(login("tester@example.com", "Wrong1!"))
            .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.code").value("AUTH401_1"))
            .andReturn().getResponse().getContentAsString();
        var missing = mvc.perform(login("unknown@example.com", "Wrong1!"))
            .andExpect(status().isUnauthorized()).andReturn().getResponse().getContentAsString();
        assertThat(missing).isEqualTo(wrong);
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder login(String email, String password) {
        return post("/auth/login").contentType("application/json")
            .content("""
                {"email":"%s","password":"%s"}
                """.formatted(email, password));
    }
}
