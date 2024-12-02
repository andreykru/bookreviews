package pl.krutikov.bookreviews.service

import pl.krutikov.bookreviews.client.GoogleBooksClient
import pl.krutikov.bookreviews.domain.Book
import pl.krutikov.bookreviews.dto.client.GoogleBooksResponse
import pl.krutikov.bookreviews.dto.request.SearchBooksRequest
import pl.krutikov.bookreviews.dto.response.BookResponse
import pl.krutikov.bookreviews.mapper.BookMapper
import pl.krutikov.bookreviews.repository.BookRepository
import spock.lang.Specification
import spock.lang.Subject

class BookServiceTest extends Specification {

    GoogleBooksClient googleBooksClient
    BookRepository bookRepository
    BookMapper bookMapper

    @Subject
    BookService bookService

    def setup() {
        googleBooksClient = Mock()
        bookRepository = Mock()
        bookMapper = Mock()
        bookService = new BookService(googleBooksClient, bookRepository, bookMapper)
    }

    def 'should search books successfully'() {
        given:
        def request = new SearchBooksRequest(
                query: 'Harry Potter',
                filter: 'ebooks',
                subject: 'fiction',
                langRestrict: 'en'
        )
        def bookItem = new GoogleBooksResponse.BookItem(
                id: '1',
                volumeInfo: new GoogleBooksResponse.VolumeInfo(
                        title: 'Harry Potter',
                        authors: ['J.K. Rowling'],
                        description: 'A magical world'
                )
        )
        def googleResponse = new GoogleBooksResponse(items: [bookItem])
        def bookResponse = new BookResponse(
                id: '1',
                title: 'Harry Potter',
                author: 'J.K. Rowling',
                description: 'A magical world'
        )
        def expectedResponse = [bookResponse]

        and:
        googleBooksClient.searchBooks(request.getQuery(), request.getFilter(), request.getLangRestrict(), request.getSubject())
                >> googleResponse
        bookMapper.toResponse(bookItem) >> bookResponse

        when:
        def result = bookService.searchBooks(request)

        then:
        result == expectedResponse
    }

    def 'should get book response by ID successfully'() {
        given:
        def bookId = '1'
        def bookEntity = new Book(
                id: bookId,
                title: 'Harry Potter',
                author: 'J.K. Rowling',
                description: 'A magical world'
        )
        def bookResponse = new BookResponse(
                id: bookId,
                title: 'Harry Potter',
                author: 'J.K. Rowling',
                description: 'A magical world'
        )

        and:
        bookRepository.findById(bookId) >> Optional.of(bookEntity)
        bookMapper.toResponse(bookEntity) >> bookResponse

        when:
        def result = bookService.getBookResponseById(bookId)

        then:
        result == bookResponse
        0 * googleBooksClient.getBookById(bookId)
        0 * bookRepository.save(bookEntity)
    }

    def 'should fetch book response from API if not found in repository'() {
        given:
        def bookId = '1'
        def googleBookItem = new GoogleBooksResponse.BookItem(
                id: bookId,
                volumeInfo: new GoogleBooksResponse.VolumeInfo(
                        title: 'Harry Potter',
                        authors: ['J.K. Rowling'],
                        description: 'A magical world'
                )
        )
        def bookEntity = new Book(
                id: bookId,
                title: 'Harry Potter',
                author: 'J.K. Rowling',
                description: 'A magical world'
        )
        def bookResponse = new BookResponse(
                id: bookId,
                title: 'Harry Potter',
                author: 'J.K. Rowling',
                description: 'A magical world'
        )

        and:
        bookRepository.findById(bookId) >> Optional.empty()
        bookMapper.toEntity(googleBookItem) >> bookEntity
        bookMapper.toResponse(bookEntity) >> bookResponse

        when:
        def result = bookService.getBookResponseById(bookId)

        then:
        result == bookResponse
        1 * googleBooksClient.getBookById(bookId) >> googleBookItem
        1 * bookRepository.save(bookEntity) >> bookEntity
    }

    def 'should return book response from repository if book found'() {
        given:
        def bookId = '1'
        def bookEntity = new Book(
                id: bookId,
                title: 'Harry Potter',
                author: 'J.K. Rowling',
                description: 'A magical world'
        )
        def bookResponse = new BookResponse(
                id: bookId,
                title: 'Harry Potter',
                author: 'J.K. Rowling',
                description: 'A magical world'
        )

        and:
        bookRepository.findById(bookId) >> Optional.of(bookEntity)
        bookMapper.toResponse(bookEntity) >> bookResponse

        when:
        def result = bookService.getBookResponseById(bookId)

        then:
        result == bookResponse
        0 * googleBooksClient.getBookById(bookId)
        0 * bookRepository.save(bookEntity)
    }

    def 'should fetch book from API if not found in repository'() {
        given:
        def bookId = '1'
        def googleBookItem = new GoogleBooksResponse.BookItem(
                id: bookId,
                volumeInfo: new GoogleBooksResponse.VolumeInfo(
                        title: 'Harry Potter',
                        authors: ['J.K. Rowling'],
                        description: 'A magical world'
                )
        )
        def bookEntity = new Book(
                id: bookId,
                title: 'Harry Potter',
                author: 'J.K. Rowling',
                description: 'A magical world'
        )

        and:
        bookRepository.findById(bookId) >> Optional.empty()
        bookMapper.toEntity(googleBookItem) >> bookEntity

        when:
        def result = bookService.getBookById(bookId)

        then:
        result == bookEntity
        1 * googleBooksClient.getBookById(bookId) >> googleBookItem
        1 * bookRepository.save(bookEntity) >> bookEntity
    }

}
