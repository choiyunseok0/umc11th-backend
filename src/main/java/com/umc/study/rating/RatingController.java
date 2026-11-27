package com.umc.study.rating;

import com.umc.study.auth.AuthInterceptor;
import com.umc.study.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ratings")
@RequiredArgsConstructor
public class RatingController {
    private final RatingService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<RatingResponse> create(@RequestAttribute(AuthInterceptor.MEMBER_ID) long memberId,
            @Valid @RequestBody CreateRatingRequest request) {
        return new ApiResponse<>(true, "COMMON201", "평점이 등록되었습니다.", service.create(memberId, request));
    }

    @PatchMapping("/{ratingId}")
    public ApiResponse<RatingResponse> update(@RequestAttribute(AuthInterceptor.MEMBER_ID) long memberId, @PathVariable long ratingId,
            @Valid @RequestBody UpdateRatingRequest request) {
        return new ApiResponse<>(true, "COMMON200", "평점이 수정되었습니다.", service.update(memberId, ratingId, request));
    }

    @DeleteMapping("/{ratingId}")
    public ApiResponse<Void> delete(@RequestAttribute(AuthInterceptor.MEMBER_ID) long memberId, @PathVariable long ratingId) {
        service.delete(memberId, ratingId);
        return new ApiResponse<>(true, "COMMON200", "평점이 삭제되었습니다.", null);
    }
}
