package pl.krutikov.bookreviews;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@Slf4j
@EnableFeignClients
@SpringBootApplication
public class BookreviewsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookreviewsApplication.class, args);
    }

    @Value("${AWS_DB_URL:NOT_DEFINED}")
    private String dbUrl;

    @Value("${AWS_DB_USERNAME:NOT_DEFINED}")
    private String dbUsername;

    @Value("${AWS_DB_PASSWORD:NOT_DEFINED}")
    private String dbPassword;

    @PostConstruct
    public void logEnvironmentVariables() {
        log.info("AWS_DB_URL: {}", dbUrl);
        log.info("AWS_DB_USERNAME: {}", dbUsername);
        log.info("AWS_DB_PASSWORD: {}", dbPassword);
    }

}
