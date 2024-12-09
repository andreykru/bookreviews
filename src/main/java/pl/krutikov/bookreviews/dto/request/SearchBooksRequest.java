package pl.krutikov.bookreviews.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import pl.krutikov.bookreviews.validation.Filter;
import pl.krutikov.bookreviews.validation.Language;
import pl.krutikov.bookreviews.validation.Subject;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchBooksRequest {

    @NotBlank
    private String query;
    @Filter
    private String filter;
    @Subject
    private String subject;
    @Language
    private String langRestrict;

}
