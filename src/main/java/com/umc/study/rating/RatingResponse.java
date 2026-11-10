package com.umc.study.rating;

public record RatingResponse(long ratingId, long memberId, long movieId, int score, String comment) {}
