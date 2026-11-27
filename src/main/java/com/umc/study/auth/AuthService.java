package com.umc.study.auth;

import com.umc.study.member.MemberRepository;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository repository;
    private final JwtUtil jwt;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final String dummyHash = encoder.encode("dummy-login-password");

    public LoginResponse login(LoginRequest request) {
        var member = repository.findCredentialsByEmail(request.email().toLowerCase(Locale.ROOT));
        String hash = member.map(MemberRepository.LoginCredentials::passwordHash).orElse(dummyHash);
        boolean matches = request.password().getBytes(StandardCharsets.UTF_8).length <= 72
            && encoder.matches(request.password(), hash);
        if (member.isEmpty() || !matches) {
            throw new InvalidCredentialsException();
        }
        var account = member.orElseThrow();
        return new LoginResponse(account.nickname(), jwt.createAccessToken(account.memberId(), account.email()));
    }
}
