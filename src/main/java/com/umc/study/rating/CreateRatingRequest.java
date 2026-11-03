package com.umc.study.rating;

import jakarta.validation.constraints.*;

public record CreateRatingRequest(@NotNull @Positive Long movieId,
    @NotNull @Min(1) @Max(5) Integer score, @Size(max = 500) String comment) {}
