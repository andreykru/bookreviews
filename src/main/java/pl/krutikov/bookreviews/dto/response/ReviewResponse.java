package pl.krutikov.bookreviews.dto.response;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ReviewResponse {

    private Long id;
    private Long userId;
    private String username;
    private String bookId;
    private String reviewText;
    private Timestamp createdAt;
    private Timestamp updatedAt;

}
