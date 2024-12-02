package pl.krutikov.bookreviews.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.krutikov.bookreviews.domain.Book;
import pl.krutikov.bookreviews.domain.Review;
import pl.krutikov.bookreviews.domain.User;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByBookId(String bookId);

    boolean existsByBookAndUser(Book book, User user);
    boolean existsByBookIdAndUserId(String bookId, Long userId);

}
