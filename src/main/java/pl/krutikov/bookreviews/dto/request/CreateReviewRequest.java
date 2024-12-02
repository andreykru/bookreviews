package pl.krutikov.bookreviews.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateReviewRequest {

    @NotBlank
    private String bookId;
    @NotBlank
    private String reviewText;

}
