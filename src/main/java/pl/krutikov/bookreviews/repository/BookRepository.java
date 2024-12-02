package pl.krutikov.bookreviews.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.krutikov.bookreviews.domain.Book;

@Repository
public interface BookRepository extends JpaRepository <Book, String> {
}
