package pl.krutikov.bookreviews.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pl.krutikov.bookreviews.domain.Book;
import pl.krutikov.bookreviews.domain.Review;
import pl.krutikov.bookreviews.domain.User;
import pl.krutikov.bookreviews.dto.request.CreateReviewRequest;
import pl.krutikov.bookreviews.dto.request.UpdateReviewRequest;
import pl.krutikov.bookreviews.dto.response.ReviewIdResponse;
import pl.krutikov.bookreviews.dto.response.ReviewResponse;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper
public interface ReviewMapper {

    @Mapping(target = "bookId", source = "review.book.id")
    @Mapping(target = "userId", source = "review.user.id")
    @Mapping(target = "username", source = "review.user.username")
    ReviewResponse toResponse(Review review);

    @Mapping(target = "reviewId", source = "review.id")
    ReviewIdResponse toIdResponse(Review review);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "book", source = "book")
    @Mapping(target = "reviewText", source = "request.reviewText")
    Review toEntity(CreateReviewRequest request, User user, Book book);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void update(UpdateReviewRequest request, @MappingTarget Review review);

}
