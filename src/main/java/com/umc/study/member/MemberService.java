package com.umc.study.member;

import com.umc.study.common.ApiException;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;
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

    public MemberInfoResponse getMyInfo(long memberId) {
        return repository.findInfoById(memberId).orElseThrow(() ->
            new ApiException(HttpStatus.NOT_FOUND, "MEMBER404_1", "존재하지 않는 회원입니다."));
    }

    @Transactional
    public MemberInfoResponse updateMyInfo(long memberId, MemberUpdateRequest request) {
        try {
            repository.updateNickname(memberId, request.nickname());
        } catch (DuplicateKeyException error) {
            throw new ApiException(HttpStatus.CONFLICT, "MEMBER409_1", "이미 사용 중인 닉네임입니다.");
        }
        return getMyInfo(memberId);
    }
}
