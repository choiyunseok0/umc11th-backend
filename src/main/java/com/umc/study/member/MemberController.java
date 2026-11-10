package com.umc.study.member;

import com.umc.study.auth.AuthInterceptor;
import com.umc.study.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<MemberResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        return new ApiResponse<>(true, "COMMON201", "회원가입이 완료되었습니다.", service.signUp(request));
    }

    @GetMapping("/me")
    public ApiResponse<MemberInfoResponse> getMyInfo(@RequestAttribute(AuthInterceptor.MEMBER_ID) long memberId) {
        return new ApiResponse<>(true, "COMMON200", "내 정보를 조회했습니다.", service.getMyInfo(memberId));
    }
}
