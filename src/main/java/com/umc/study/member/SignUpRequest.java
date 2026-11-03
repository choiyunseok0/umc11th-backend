package com.umc.study.member;

import jakarta.validation.constraints.*;

public record SignUpRequest(
    @NotBlank @Email @Size(max = 254) String email,
    @NotBlank @Pattern(regexp = "(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*])[!-~]{8,72}",
        message = "비밀번호는 영문 대소문자, 숫자, 특수문자를 포함한 8~72자여야 합니다.") String password,
    @NotBlank @Size(min = 2, max = 12) String nickname
) {}
