package com.umc.study.rating;

import com.umc.study.common.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingRepository repository;

    public RatingResponse create(long memberId, CreateRatingRequest request) {
        if (!repository.memberExists(memberId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "MEMBER404_1", "존재하지 않는 회원입니다.");
        }
        long ratingId = repository.insert(memberId, request);
        return new RatingResponse(ratingId, memberId, request.movieId(), request.score(), request.comment());
    }

    @Transactional
    public RatingResponse update(long memberId, long ratingId, UpdateRatingRequest request) {
        var rating = requireOwner(memberId, ratingId);
        repository.update(ratingId, request);
        return new RatingResponse(ratingId, memberId, rating.movieId(), request.score(), request.comment());
    }

    @Transactional
    public void delete(long memberId, long ratingId) {
        requireOwner(memberId, ratingId);
        repository.delete(ratingId);
    }

    private RatingResponse requireOwner(long memberId, long ratingId) {
        var rating = repository.lockById(ratingId).orElseThrow(() ->
            new ApiException(HttpStatus.NOT_FOUND, "RATING404_1", "존재하지 않는 평점입니다."));
        if (rating.memberId() != memberId) {
            throw new ApiException(HttpStatus.FORBIDDEN, "RATING403_1", "본인이 작성한 평점만 수정하거나 삭제할 수 있습니다.");
        }
        return rating;
    }
}
