package pl.krutikov.bookreviews.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateReviewRequest {

    @NotBlank
    private String reviewText;

}
