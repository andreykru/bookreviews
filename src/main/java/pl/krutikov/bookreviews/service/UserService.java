package pl.krutikov.bookreviews.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import pl.krutikov.bookreviews.domain.User;
import pl.krutikov.bookreviews.dto.request.RegisterUserRequest;
import pl.krutikov.bookreviews.dto.response.UserIdResponse;
import pl.krutikov.bookreviews.exception.BadRequestException;
import pl.krutikov.bookreviews.exception.NotFoundException;
import pl.krutikov.bookreviews.logging.Logging;
import pl.krutikov.bookreviews.mapper.UserMapper;
import pl.krutikov.bookreviews.repository.UserRepository;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = this.findByEmail(email);

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), Collections.emptyList());
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(String.format("No user found with email: %s", email)));
    }

    @Logging
    @SneakyThrows
    public UserIdResponse registerNewUser(RegisterUserRequest request) {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<Boolean> emailCheckFuture = executor.submit(
                    () -> userRepository.findByEmail(request.getEmail()).isPresent()
            );
            Future<Boolean> usernameCheckFuture = executor.submit(
                    () -> userRepository.findByUsername(request.getUsername()).isPresent()
            );

            if (emailCheckFuture.get()) {
                throw new BadRequestException(String.format("User with email: %s is already registered", request.getEmail()));
            }
            if (usernameCheckFuture.get()) {
                throw new BadRequestException(String.format("User with username: %s is already registered", request.getUsername()));
            }

            User newUser = userMapper.toEntity(request);
            User savedUser = userRepository.save(newUser);

            return userMapper.toResponse(savedUser);
        }
    }

}
