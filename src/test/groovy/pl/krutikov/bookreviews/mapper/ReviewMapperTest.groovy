package pl.krutikov.bookreviews.mapper

import org.mapstruct.factory.Mappers
import pl.krutikov.bookreviews.domain.Book
import pl.krutikov.bookreviews.domain.Review
import pl.krutikov.bookreviews.domain.User
import pl.krutikov.bookreviews.dto.request.CreateReviewRequest
import pl.krutikov.bookreviews.dto.request.UpdateReviewRequest
import spock.lang.Specification
import spock.lang.Subject

import java.sql.Timestamp
import java.time.Instant


class ReviewMapperTest extends Specification {

    @Subject
    ReviewMapper reviewMapper

    def setup() {
        reviewMapper = Mappers.getMapper(ReviewMapper)
    }

    def 'should map Review to ReviewResponse'() {
        given:
        def currentTimestamp = Timestamp.from(Instant.now())
        def user = new User(
                id: 1L,
                username: 'testuser'
        )
        def book = new Book(
                id: 'book123',
                title: 'Harry Potter',
                author: 'J.K. Rowling',
                description: 'Fantasy novel'
        )
        def review = new Review(
                id: 1L,
                reviewText: 'Great book!',
                createdAt: currentTimestamp,
                updatedAt: currentTimestamp,
                user: user,
                book: book
        )

        when:
        def response = reviewMapper.toResponse(review)

        then:
        response != null
        response.id == review.id
        response.userId == review.user.id
        response.username == review.user.username
        response.bookId == review.book.id
        response.reviewText == review.reviewText
        response.createdAt == review.createdAt
        response.updatedAt == review.updatedAt
    }

    def 'should map Review to ReviewIdResponse'() {
        given:
        def review = new Review(id: 1L)

        when:
        def response = reviewMapper.toIdResponse(review)

        then:
        response != null
        response.reviewId == review.id
    }

    def 'should map CreateReviewRequest to Review entity'() {
        given:
        def user = new User(
                id: 1L,
                username: 'testuser'
        )
        def book = new Book(
                id: 'book123',
                title: 'Harry Potter',
                author: 'J.K. Rowling',
                description: 'Fantasy novel'
        )
        def request = new CreateReviewRequest(
                bookId: book.id,
                reviewText: 'An amazing book!'
        )

        when:
        def review = reviewMapper.toEntity(request, user, book)

        then:
        review != null
        review.id == null
        review.reviewText == request.reviewText
        review.user == user
        review.book == book
    }

    def 'should update Review entity with UpdateReviewRequest'() {
        given:
        def review = new Review(
                id: 1L,
                reviewText: 'Old text'
        )
        def request = new UpdateReviewRequest(
                reviewText: 'Updated text'
        )

        when:
        reviewMapper.update(request, review)

        then:
        review != null
        review.reviewText == request.reviewText
    }

}
