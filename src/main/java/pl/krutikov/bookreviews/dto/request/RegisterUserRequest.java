package pl.krutikov.bookreviews.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import pl.krutikov.bookreviews.validation.Password;
import pl.krutikov.bookreviews.validation.Username;

@Data
public class RegisterUserRequest {

    @Username
    private String username;
    @NotNull @Email
    private String email;
    @Password
    private String password;

}
