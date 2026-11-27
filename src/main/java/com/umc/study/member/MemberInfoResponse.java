package com.umc.study.member;

import java.time.LocalDateTime;

public record MemberInfoResponse(String email, String nickname, LocalDateTime createdAt) {}
