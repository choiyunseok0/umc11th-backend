package com.umc.study.auth;

import com.umc.study.common.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {
    public static final String MEMBER_ID = "authenticatedMemberId";
    private final JwtUtil jwt;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || header.isBlank()) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "AUTH401_2", "로그인이 필요한 요청입니다.");
        }
        if (!header.regionMatches(true, 0, "Bearer ", 0, 7) || header.substring(7).isBlank()) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "AUTH401_3", "유효하지 않은 토큰입니다.");
        }
        request.setAttribute(MEMBER_ID, jwt.getMemberId(header.substring(7)));
        return true;
    }
}
