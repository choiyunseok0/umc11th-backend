package com.umc.study.controller;

import com.umc.study.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;

@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
public class RentalController {
    private final RentalService rentalService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> createRental(@RequestBody Map<String, Object> body) {
        long userId = positiveId(body.get("userId"), "userId");
        long bookId = positiveId(body.get("bookId"), "bookId");
        rentalService.createRental(userId, bookId);
        return Map.of("message", "대여 기록이 생성되었습니다!");
    }

    private long positiveId(Object value, String field) {
        if (!(value instanceof Number number)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + "는 양의 정수여야 합니다.");
        }
        long id = number.longValue();
        if (id <= 0 || number.doubleValue() != (double) id) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + "는 양의 정수여야 합니다.");
        }
        return id;
    }
}
