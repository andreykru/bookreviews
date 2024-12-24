package pl.krutikov.bookreviews.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.krutikov.bookreviews.domain.Book;
import pl.krutikov.bookreviews.domain.Review;
import pl.krutikov.bookreviews.domain.User;
import pl.krutikov.bookreviews.dto.request.CreateReviewRequest;
import pl.krutikov.bookreviews.dto.request.UpdateReviewRequest;
import pl.krutikov.bookreviews.dto.response.ReviewIdResponse;
import pl.krutikov.bookreviews.dto.response.ReviewResponse;
import pl.krutikov.bookreviews.exception.BadRequestException;
import pl.krutikov.bookreviews.exception.NotFoundException;
import pl.krutikov.bookreviews.logging.Logging;
import pl.krutikov.bookreviews.mapper.ReviewMapper;
import pl.krutikov.bookreviews.repository.ReviewRepository;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookService bookService;
    private final UserService userService;
    private final ReviewMapper reviewMapper;

    @Logging
    public List<ReviewResponse> getReviewsByBookId(String bookId) {
        return reviewRepository.findByBookId(bookId).stream()
                .map(reviewMapper::toResponse)
                .toList();
    }

    @Logging
    @SneakyThrows
    public ReviewIdResponse createReview(CreateReviewRequest request) {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            String username = getAuthenticatedUsername();

            Future<User> userFuture = executor.submit(() -> userService.findByEmail(username));
            Future<Book> bookFuture = executor.submit(() -> bookService.getBookById(request.getBookId()));

            User user = userFuture.get();
            Book book = bookFuture.get();

            checkReviewAlreadyExists(book, user);

            Review review = reviewMapper.toEntity(request, user, book);
            Review reviewWithId = reviewRepository.save(review);

            return reviewMapper.toIdResponse(reviewWithId);
        }
    }

    @Logging
    public ReviewIdResponse updateReview(Long reviewId, UpdateReviewRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException(String.format("No review with id: %s found", reviewId)));

        checkUserIsReviewAuthor(review);
        reviewMapper.update(request, review);
        reviewRepository.save(review);

        return reviewMapper.toIdResponse(review);
    }

    @Logging
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException(String.format("No review with id: %s found", reviewId)));

        checkUserIsReviewAuthor(review);
        reviewRepository.delete(review);
    }

    private void checkReviewAlreadyExists(Book book, User user) {
        if (reviewRepository.existsByBookIdAndUserId(book.getId(), user.getId()))
            throw new BadRequestException("User has already left a review for this book");
    }

    private void checkUserIsReviewAuthor(Review review) {
        if (!Objects.equals(review.getUser().getEmail(), getAuthenticatedUsername()))
            throw new BadRequestException("User is not allowed to modify this review");
    }

    private String getAuthenticatedUsername() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .map(Principal::getName)
                .orElseThrow(() -> new BadRequestException("Unable to retrieve 'username' from authentication context"));
    }

}
