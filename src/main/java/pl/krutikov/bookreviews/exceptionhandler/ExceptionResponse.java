package pl.krutikov.bookreviews.exceptionhandler;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExceptionResponse {

    private LocalDateTime timestamp;
    private Integer status;
    private String code;
    private Map<String, String> details;

}
