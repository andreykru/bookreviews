package pl.krutikov.bookreviews.controller

import com.fasterxml.jackson.databind.ObjectMapper
import feign.FeignException
import feign.Request
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import pl.krutikov.bookreviews.dto.request.SearchBooksRequest
import pl.krutikov.bookreviews.dto.response.BookResponse
import pl.krutikov.bookreviews.exceptionhandler.GlobalExceptionHandler
import pl.krutikov.bookreviews.service.BookService
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

class BookControllerTest extends Specification {

    GlobalExceptionHandler handler
    ObjectMapper objectMapper
    BookService bookService
    MockMvc mockMvc

    @Subject
    BookController bookController

    def url = '/api/v1/books'

    def setup() {
        handler = new GlobalExceptionHandler()
        objectMapper = new ObjectMapper()
        bookService = Mock()
        bookController = new BookController(bookService)
        mockMvc = MockMvcBuilders.standaloneSetup(bookController)
                .setControllerAdvice(handler)
                .build()
    }


    def 'should search books successfully'() {
        given:
        def request = new SearchBooksRequest(
                query: 'Harry Potter',
                filter: 'ebooks',
                subject: 'fiction',
                langRestrict: 'en'
        )
        def bookResponse = new BookResponse(
                id: '1',
                title: 'Harry Potter',
                author: 'J.K. Rowling',
                description: 'Magic world'
        )
        def expectedResponse = [bookResponse]

        and:
        bookService.searchBooks(request) >> expectedResponse

        when:
        def result = mockMvc.perform(get(url)
                .param('query', request.getQuery())
                .param('filter', request.getFilter())
                .param('subject', request.getSubject())
                .param('langRestrict', request.getLangRestrict())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn()

        then:
        result.response.status == HttpStatus.OK.value()
        result.response.contentAsString == objectMapper.writeValueAsString(expectedResponse)
    }

    @Unroll
    def 'should return 400 when searchBooks input is invalid: #description'() {
        given:
        def request = new SearchBooksRequest(
                query: query,
                filter: filter,
                subject: subject,
                langRestrict: language
        )

        when:
        def result = mockMvc.perform(get(url)
                .param('query', request.getQuery())
                .param('filter', request.getFilter())
                .param('subject', request.getSubject())
                .param('langRestrict', request.getLangRestrict())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn()

        then:
        0 * bookService.searchBooks(request)
        result.response.status == HttpStatus.BAD_REQUEST.value()

        where:
        query          | filter    | subject      | language  | description
        ''             | 'ebooks'  | 'fiction'    | 'en'      | 'Missing query'
        'Harry Potter' | 'invalid' | 'fiction'    | 'en'      | 'Invalid filter'
        'Harry Potter' | 'ebooks'  | '1234567890' | 'en'      | 'Invalid subject'
        'Harry Potter' | 'ebooks'  | 'fiction'    | 'invalid' | 'Invalid language'
        '            ' | 'ebooks'  | 'fiction'    | 'en'      | 'Query is only spaces'
    }

    def 'should get book by ID successfully'() {
        given:
        def bookId = '1'
        def bookResponse = new BookResponse(
                id: bookId,
                title: 'Harry Potter',
                author: 'J.K. Rowling',
                description: 'Magic world'
        )

        and:
        bookService.getBookResponseById(bookId) >> bookResponse

        when:
        def result = mockMvc.perform(get("$url/$bookId")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn()

        then:
        result.response.status == HttpStatus.OK.value()
        result.response.contentAsString == objectMapper.writeValueAsString(bookResponse)
    }


    def 'should return 404 when book is not found'() {
        given:
        def bookId = 'some_book_id'

        and:
        def request = Request.create(
                Request.HttpMethod.GET,
                "$url/$bookId",
                [:],
                null,
                null,
                null
        )
        bookService.getBookResponseById(bookId) >> {
            throw new FeignException.NotFound(
                    'Book not found',
                    request,
                    null,
                    null
            )
        }

        when:
        def result = mockMvc.perform(get("$url/$bookId")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn()

        then:
        result.response.status == HttpStatus.NOT_FOUND.value()
        result.response.contentAsString.contains('Book not found')
    }
}
