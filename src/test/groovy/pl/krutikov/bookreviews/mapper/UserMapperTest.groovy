package pl.krutikov.bookreviews.mapper

import org.mapstruct.factory.Mappers
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import pl.krutikov.bookreviews.domain.User
import pl.krutikov.bookreviews.dto.request.RegisterUserRequest
import pl.krutikov.bookreviews.mapper.encoder.PasswordEncoderMapper
import spock.lang.Specification
import spock.lang.Subject

class UserMapperTest extends Specification {

    PasswordEncoder passwordEncoder
    PasswordEncoderMapper passwordEncoderMapper

    @Subject
    UserMapper userMapper

    def setup() {
        passwordEncoder = new BCryptPasswordEncoder()
        passwordEncoderMapper = new PasswordEncoderMapper(passwordEncoder)
        userMapper = Mappers.getMapper(UserMapper)
        ((UserMapperImpl) userMapper).passwordEncoderMapper = passwordEncoderMapper
    }

    def 'should map RegisterUserRequest to User entity with encoded password'() {
        given:
        def userRequest = new RegisterUserRequest(
                username: 'testuser',
                email: 'test@example.com',
                password: 'plaintextpassword'
        )

        when:
        def user = userMapper.toEntity(userRequest)

        then:
        user != null
        user.username == userRequest.username
        user.email == userRequest.email
        user.password != userRequest.password
        passwordEncoder.matches(userRequest.password, user.password)
    }

    def 'should map User entity to UserIdResponse'() {
        given:
        def user = new User(
                id: 1L,
                username: 'testuser',
                email: 'test@example.com',
                password: 'encodedpassword'
        )

        when:
        def response = userMapper.toResponse(user)

        then:
        response != null
        response.id == user.id
    }

}
