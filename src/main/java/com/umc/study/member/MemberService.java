package com.umc.study.member;

import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository repository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public MemberResponse signUp(SignUpRequest request) {
        String email = request.email().toLowerCase(Locale.ROOT);
        long id = repository.insert(email, request.nickname(), encoder.encode(request.password()));
        return new MemberResponse(id, email, request.nickname());
    }
}
