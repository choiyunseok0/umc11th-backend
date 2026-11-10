package com.umc.study.rating;

import com.umc.study.auth.AuthInterceptor;
import com.umc.study.auth.JwtUtil;
import com.umc.study.common.ApiExceptionHandler;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RatingAuthTest {
    @Test
    void onlyTokenOwnerCanChangeRating() throws Exception {
        var jwt = new JwtUtil("test-only-signing-key-0123456789abcdef", 3600);
        String owner = "Bearer " + jwt.createAccessToken(1, "a@example.com");
        String other = "Bearer " + jwt.createAccessToken(2, "b@example.com");
        var repo = mock(RatingRepository.class);
        var create = new CreateRatingRequest(5L, 4, null);
        when(repo.memberExists(1)).thenReturn(true);
        when(repo.insert(1, create)).thenReturn(7L).thenThrow(new DuplicateKeyException("rating"));
        when(repo.lockById(7)).thenReturn(Optional.of(new RatingResponse(7, 1, 5, 4, null)));
        var mvc = MockMvcBuilders.standaloneSetup(new RatingController(new RatingService(repo)))
            .addMappedInterceptors(new String[]{"/ratings", "/ratings/**"}, new AuthInterceptor(jwt))
            .setControllerAdvice(new ApiExceptionHandler(), new RatingExceptionHandler()).build();

        mvc.perform(post("/ratings").contentType("application/json").content("""
            {"movieId":5,"score":4}
            """)).andExpect(status().isUnauthorized()).andExpect(jsonPath("$.code").value("AUTH401_2"));
        verifyNoInteractions(repo);

        var register = post("/ratings").header("Authorization", owner).contentType("application/json")
            .content("""
                {"movieId":5,"score":4}
                """);
        mvc.perform(register).andExpect(status().isCreated()).andExpect(jsonPath("$.result.memberId").value(1));
        mvc.perform(register).andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("RATING409_1"));

        mvc.perform(patch("/ratings/7").header("Authorization", other).contentType("application/json")
            .content("""
                {"score":3}
                """)).andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value("RATING403_1"));
        mvc.perform(delete("/ratings/7").header("Authorization", other))
            .andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value("RATING403_1"));
        verify(repo, never()).update(anyLong(), any());
        verify(repo, never()).delete(anyLong());

        mvc.perform(patch("/ratings/7").header("Authorization", owner).contentType("application/json")
            .content("""
                {"score":3}
                """)).andExpect(status().isOk()).andExpect(jsonPath("$.result.score").value(3));
        mvc.perform(delete("/ratings/99999").header("Authorization", owner))
            .andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value("RATING404_1"));
        mvc.perform(delete("/ratings/7").header("Authorization", owner))
            .andExpect(status().isOk()).andExpect(jsonPath("$.code").value("COMMON200"))
            .andExpect(jsonPath("$.result").value(org.hamcrest.Matchers.nullValue()));
        verify(repo).delete(7);
    }
}
