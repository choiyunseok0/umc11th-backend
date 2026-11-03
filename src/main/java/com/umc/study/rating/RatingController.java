package com.umc.study.rating;

import com.umc.study.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members/{memberId}/ratings")
@RequiredArgsConstructor
public class RatingController {
    private final RatingService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<RatingResponse> create(@PathVariable long memberId,
            @Valid @RequestBody CreateRatingRequest request) {
        return new ApiResponse<>(true, "COMMON201", "평점이 등록되었습니다.", service.create(memberId, request));
    }

    @PatchMapping("/{ratingId}")
    public ApiResponse<RatingResponse> update(@PathVariable long memberId, @PathVariable long ratingId,
            @Valid @RequestBody UpdateRatingRequest request) {
        return new ApiResponse<>(true, "COMMON200", "평점이 수정되었습니다.", service.update(memberId, ratingId, request));
    }

    @DeleteMapping("/{ratingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long memberId, @PathVariable long ratingId) {
        service.delete(memberId, ratingId);
    }
}
