package pl.krutikov.bookreviews.mapper

import org.mapstruct.factory.Mappers
import pl.krutikov.bookreviews.domain.Book
import pl.krutikov.bookreviews.dto.client.GoogleBooksResponse
import spock.lang.Specification
import spock.lang.Subject

class BookMapperTest extends Specification {

    @Subject
    BookMapper bookMapper

    def setup() {
        bookMapper = Mappers.getMapper(BookMapper)
    }

    def 'should map Book to BookResponse'() {
        given:
        def book = new Book(
                id: 123,
                title: 'Harry Potter and the Sorcerer\'s Stone',
                author: 'J.K. Rowling',
                description: 'A young wizard embarks on a journey of magical adventure.'
        )

        when:
        def response = bookMapper.toResponse(book)

        then:
        response != null
        response.id == book.id
        response.title == book.title
        response.author == book.author
        response.description == book.description
    }

    def 'should map GoogleBooksResponse to BookResponse'() {
        given:
        def bookItem = new GoogleBooksResponse.BookItem(
                id: 123,
                volumeInfo: new GoogleBooksResponse.VolumeInfo(
                        title: 'Harry Potter and the Sorcerers Stone',
                        authors: ['J.K. Rowling'],
                        description: 'A young wizard embarks on a journey of magical adventure.'
                )
        )

        when:
        def response = bookMapper.toResponse(bookItem)

        then:
        response != null
        response.id == bookItem.id
        response.title == bookItem.volumeInfo.title
        response.author == 'J.K. Rowling'
        response.description == bookItem.volumeInfo.description
    }

    def 'should map GoogleBooksResponse to Book'() {
        given:
        def bookItem = new GoogleBooksResponse.BookItem(
                id: 123,
                volumeInfo: new GoogleBooksResponse.VolumeInfo(
                        title: 'Harry Potter and the Sorcerers Stone',
                        authors: ['J.K. Rowling'],
                        description: 'A young wizard embarks on a journey of magical adventure.'
                )
        )

        when:
        def book = bookMapper.toEntity(bookItem)

        then:
        book != null
        book.id == bookItem.id
        book.title == bookItem.volumeInfo.title
        book.author == 'J.K. Rowling'
        book.description == bookItem.volumeInfo.description
    }

    def 'should correctly map authors using mapAuthors method'() {
        given:
        def bookItem = new GoogleBooksResponse.BookItem(
                id: 123,
                volumeInfo: new GoogleBooksResponse.VolumeInfo(
                        title: 'Harry Potter and the Sorcerers Stone',
                        authors: ['J.K. Rowling', 'Second Author'],
                        description: 'A young wizard embarks on a journey of magical adventure.'
                )
        )

        when:
        def author = BookMapper.mapAuthors(bookItem)

        then:
        author == 'J.K. Rowling, Second Author'
    }

}
