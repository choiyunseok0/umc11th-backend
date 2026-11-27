package com.umc.study.rating;

import com.umc.study.common.ApiResponse;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice(assignableTypes = RatingController.class)
public class RatingExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> validation(MethodArgumentNotValidException error) {
        var field = error.getBindingResult().getFieldError();
        return ApiResponse.failure("INVALID_INPUT",
            field == null ? "잘못된 입력입니다." : field.getField() + ": " + field.getDefaultMessage());
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> malformed() {
        return ApiResponse.failure("INVALID_INPUT", "요청 값을 확인해주세요.");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<Void> duplicate() {
        return ApiResponse.failure("RATING409_1", "이미 이 영화에 평점을 남겼습니다.");
    }
}
