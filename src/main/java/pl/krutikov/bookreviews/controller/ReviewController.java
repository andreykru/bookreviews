package pl.krutikov.bookreviews.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.krutikov.bookreviews.dto.request.CreateReviewRequest;
import pl.krutikov.bookreviews.dto.request.UpdateReviewRequest;
import pl.krutikov.bookreviews.dto.response.ReviewIdResponse;
import pl.krutikov.bookreviews.dto.response.ReviewResponse;
import pl.krutikov.bookreviews.service.ReviewService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/book/{bookId}")
    public List<ReviewResponse> getReviewsByBookId(@NotBlank @PathVariable String bookId) {
        return reviewService.getReviewsByBookId(bookId);
    }

    @PostMapping
    public ReviewIdResponse createReview(@Valid @RequestBody CreateReviewRequest request) {
        return reviewService.createReview(request);
    }

    @PutMapping("/{reviewId}")
    public ReviewIdResponse updateReview(@Positive @PathVariable Long reviewId,
                                         @Valid @RequestBody UpdateReviewRequest request) {
        return reviewService.updateReview(reviewId, request);
    }

    @DeleteMapping("/{reviewId}")
    public void deleteReview(@Positive @PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
    }

}
