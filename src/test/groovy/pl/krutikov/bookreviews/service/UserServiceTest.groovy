package pl.krutikov.bookreviews.service

import pl.krutikov.bookreviews.domain.User
import pl.krutikov.bookreviews.dto.request.RegisterUserRequest
import pl.krutikov.bookreviews.dto.response.UserIdResponse
import pl.krutikov.bookreviews.exception.BadRequestException
import pl.krutikov.bookreviews.mapper.UserMapper
import pl.krutikov.bookreviews.repository.UserRepository
import spock.lang.Specification
import spock.lang.Subject

class UserServiceTest extends Specification {

    UserRepository userRepository
    UserMapper userMapper

    @Subject
    UserService userService

    def setup() {
        userRepository = Mock()
        userMapper = Mock()
        userService = new UserService(userRepository, userMapper)
    }

    def 'should register new user successfully'() {
        given:
        def request = new RegisterUserRequest(
                email: 'newuser@example.com',
                username: 'newuser',
                password: 'password123'
        )
        def userEntity = new User(
                email: request.getEmail(),
                username: request.getUsername(),
                password: request.getPassword()
        )
        def userIdResponse = new UserIdResponse(id: 1)

        and:
        userRepository.findByEmail(request.getEmail()) >> Optional.empty()
        userRepository.findByUsername(request.getUsername()) >> Optional.empty()
        userMapper.toEntity(request) >> userEntity
        userMapper.toResponse(userEntity) >> userIdResponse
        userRepository.save(userEntity) >> userEntity

        when:
        def result = userService.registerNewUser(request)

        then:
        result == userIdResponse
        1 * userRepository.save(userEntity) >> userEntity
    }

    def 'should throw BadRequestException when email is already registered'() {
        given:
        def request = new RegisterUserRequest(
                email: 'existingemail@example.com',
                username: 'newuser',
                password: 'password123'
        )

        and:
        userRepository.findByEmail(request.getEmail()) >> Optional.of(new User(email: request.getEmail()))
        userRepository.findByUsername(request.getUsername()) >> Optional.empty()

        when:
        userService.registerNewUser(request)

        then:
        thrown(BadRequestException)
        0 * userRepository.save(_)
    }

    def 'should throw BadRequestException when username is already registered'() {
        given:
        def request = new RegisterUserRequest(
                email: 'newuser@example.com',
                username: 'existingusername',
                password: 'password123'
        )

        and:
        userRepository.findByEmail(request.getEmail()) >> Optional.empty()
        userRepository.findByUsername(request.getUsername()) >> Optional.of(new User(username: request.getUsername()))

        when:
        userService.registerNewUser(request)

        then:
        thrown(BadRequestException)
        0 * userRepository.save(_)
    }

}
