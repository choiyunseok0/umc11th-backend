package com.umc.study.auth;

import com.umc.study.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice(assignableTypes = AuthController.class)
public class AuthExceptionHandler {
    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Void> invalidCredentials(InvalidCredentialsException exception) {
        return ApiResponse.failure("AUTH401_1", exception.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> invalidInput() {
        return ApiResponse.failure("INVALID_INPUT", "이메일과 비밀번호 입력값을 확인해주세요.");
    }
}
