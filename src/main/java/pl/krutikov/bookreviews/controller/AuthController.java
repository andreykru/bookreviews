package pl.krutikov.bookreviews.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.krutikov.bookreviews.dto.request.RegisterUserRequest;
import pl.krutikov.bookreviews.dto.response.UserIdResponse;
import pl.krutikov.bookreviews.service.UserService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public UserIdResponse register(@Valid @RequestBody RegisterUserRequest request) {
        return userService.registerNewUser(request);
    }

}
