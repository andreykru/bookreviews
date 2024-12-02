package pl.krutikov.bookreviews.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.krutikov.bookreviews.dto.request.SearchBooksRequest;
import pl.krutikov.bookreviews.dto.response.BookResponse;
import pl.krutikov.bookreviews.service.BookService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookService bookService;

    @GetMapping
    public List<BookResponse> searchBooks(@Valid SearchBooksRequest request) {
        return bookService.searchBooks(request);
    }

    @GetMapping("/{id}")
    public BookResponse getBookById(@NotBlank @PathVariable String id) {
        return bookService.getBookResponseById(id);
    }

}

