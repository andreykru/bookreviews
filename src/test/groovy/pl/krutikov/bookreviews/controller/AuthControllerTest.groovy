package pl.krutikov.bookreviews.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import pl.krutikov.bookreviews.dto.request.RegisterUserRequest
import pl.krutikov.bookreviews.dto.response.UserIdResponse
import pl.krutikov.bookreviews.exceptionhandler.GlobalExceptionHandler
import pl.krutikov.bookreviews.service.UserService
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post


class AuthControllerTest extends Specification {

    GlobalExceptionHandler handler
    ObjectMapper objectMapper
    UserService userService
    MockMvc mockMvc

    @Subject
    AuthController authController

    def url = '/api/v1/auth/register'

    def setup() {
        handler = new GlobalExceptionHandler()
        objectMapper = new ObjectMapper()
        userService = Mock()
        authController = new AuthController(userService)
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(handler)
                .build()
    }

    def 'should register new user successfully'() {
        given:
        def registerRequest = new RegisterUserRequest(
                username: 'testuser',
                email: 'test@example.com',
                password: 'password123'
        )
        def registerResponse = new UserIdResponse(id: 1L)

        and:
        userService.registerNewUser(registerRequest) >> registerResponse

        when:
        def result = mockMvc.perform(post(url)
                .content(objectMapper.writeValueAsString(registerRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn()

        then:
        result.response.status == HttpStatus.OK.value()
        result.response.contentAsString == objectMapper.writeValueAsString(registerResponse)
    }

    @Unroll
    def 'should return 400 when request is invalid: #description'() {
        given:
        def registerRequest = new RegisterUserRequest(
                username: username,
                email: email,
                password: password
        )

        when:
        def result = mockMvc.perform(post(url)
                .content(objectMapper.writeValueAsString(registerRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn()

        then:
        result.response.status == HttpStatus.BAD_REQUEST.value()

        where:
        username        | email              | password       | description
        null            | 'test@example.com' | 'password123'  | 'username is null'
        ''              | 'test@example.com' | 'password123'  | 'username is empty'
        'testuser'      | null               | 'password123'  | 'email is null'
        'testuser'      | 'invalid-email'    | 'password123'  | 'email is invalid'
        'testuser'      | 'test@example.com' | null           | 'password is null'
        'testuser'      | 'test@example.com' | ''             | 'password is empty'
        'testuser'      | 'test@example.com' | 'p'            | 'password too short'
        'a'.repeat(101) | 'test@example.com' | 'password123'  | 'username too long'
        'testuser'      | 'test@example.com' | 'a'.repeat(21) | 'password too long'
    }

}
