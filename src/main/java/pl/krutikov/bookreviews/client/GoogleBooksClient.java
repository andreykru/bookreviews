package pl.krutikov.bookreviews.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import pl.krutikov.bookreviews.config.FeignConfig;
import pl.krutikov.bookreviews.dto.client.GoogleBooksResponse;
import pl.krutikov.bookreviews.logging.Logging;

@FeignClient(
        name = "googleBooksClient",
        url = "https://www.googleapis.com/books/v1",
        configuration = FeignConfig.class
)
public interface GoogleBooksClient {

    @Logging
    @GetMapping("/volumes")
    GoogleBooksResponse searchBooks(
            @RequestParam("q") String query,
            @RequestParam String filter,
            @RequestParam String langRestrict,
            @RequestParam String subject
    );

    @Logging
    @GetMapping("/volumes/{id}")
    GoogleBooksResponse.BookItem getBookById(@PathVariable String id);

}
