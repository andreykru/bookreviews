package pl.krutikov.bookreviews;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class BookreviewsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookreviewsApplication.class, args);
    }


}
