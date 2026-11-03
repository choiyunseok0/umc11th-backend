package com.umc.study.common;

public record ApiResponse<T>(boolean isSuccess, String code, String message, T result) {
    public static ApiResponse<Void> failure(String code, String message) {
        return new ApiResponse<>(false, code, message, null);
    }
}
