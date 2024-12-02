package pl.krutikov.bookreviews.config;

import feign.FeignException;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class FeignConfig {

    @Bean
    public ErrorDecoder googleBooksErrorDecoder() {
        return (methodKey, response) -> {
            log.error("Error in Google Books Feign Client. Method: {}, Status code: {}, Reason: {}", methodKey, response.status(), response.reason());

            if (response.status() == 503) {
                log.warn("Transforming 503 error into 404: Book not found.");

                String message = String.format("[%d %s] during [%s] to [%s]", 404, "Book not found", response.request().httpMethod(), response.request().url());

                return new FeignException.NotFound(
                        message,
                        response.request(),
                        null,
                        null
                );
            }

            return new ErrorDecoder.Default().decode(methodKey, response);
        };
    }

}
