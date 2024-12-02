package pl.krutikov.bookreviews.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookResponse {

    private String id;
    private String title;
    private String author;
    private String description;

}
