package pl.krutikov.bookreviews.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.krutikov.bookreviews.domain.Review;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByBookId(String bookId);

    boolean existsByBookIdAndUserId(String bookId, Long userId);

}
