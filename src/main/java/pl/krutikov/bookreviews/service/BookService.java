package pl.krutikov.bookreviews.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.krutikov.bookreviews.client.GoogleBooksClient;
import pl.krutikov.bookreviews.domain.Book;
import pl.krutikov.bookreviews.dto.client.GoogleBooksResponse;
import pl.krutikov.bookreviews.dto.request.SearchBooksRequest;
import pl.krutikov.bookreviews.dto.response.BookResponse;
import pl.krutikov.bookreviews.logging.Logging;
import pl.krutikov.bookreviews.mapper.BookMapper;
import pl.krutikov.bookreviews.repository.BookRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final GoogleBooksClient googleBooksClient;
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Logging
    public List<BookResponse> searchBooks(SearchBooksRequest request) {
        GoogleBooksResponse response = googleBooksClient.searchBooks(
                request.getQuery(),
                request.getFilter(),
                request.getLangRestrict(),
                request.getSubject()
        );

        return Optional.ofNullable(response)
                .map(GoogleBooksResponse::getItems)
                .orElse(Collections.emptyList())
                .stream()
                .map(bookMapper::toResponse)
                .toList();
    }

    @Logging
    public BookResponse getBookResponseById(String bookId) {
        Book book = getBookFromRepositoryOrFetchFromBooksApi(bookId);
        return bookMapper.toResponse(book);
    }

    @Logging
    public Book getBookById(String bookId) {
        return getBookFromRepositoryOrFetchFromBooksApi(bookId);
    }

    private Book getBookFromRepositoryOrFetchFromBooksApi(String bookId) {
        return bookRepository.findById(bookId)
                .orElseGet(() -> {
                    GoogleBooksResponse.BookItem item = googleBooksClient.getBookById(bookId);
                    Book book = bookMapper.toEntity(item);
                    return bookRepository.save(book);
                });
    }

}
