package pl.krutikov.bookreviews.service

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import pl.krutikov.bookreviews.domain.Book
import pl.krutikov.bookreviews.domain.Review
import pl.krutikov.bookreviews.domain.User
import pl.krutikov.bookreviews.dto.request.CreateReviewRequest
import pl.krutikov.bookreviews.dto.request.UpdateReviewRequest
import pl.krutikov.bookreviews.dto.response.ReviewIdResponse
import pl.krutikov.bookreviews.dto.response.ReviewResponse
import pl.krutikov.bookreviews.exception.BadRequestException
import pl.krutikov.bookreviews.exception.NotFoundException
import pl.krutikov.bookreviews.mapper.ReviewMapper
import pl.krutikov.bookreviews.repository.ReviewRepository
import spock.lang.Specification
import spock.lang.Subject

class ReviewServiceTest extends Specification {

    ReviewRepository reviewRepository
    BookService bookService
    UserService userService
    ReviewMapper reviewMapper

    @Subject
    ReviewService reviewService

    def setup() {
        reviewRepository = Mock()
        bookService = Mock()
        userService = Mock()
        reviewMapper = Mock()
        reviewService = new ReviewService(reviewRepository, bookService, userService, reviewMapper)
    }

    def 'should get reviews by bookId'() {
        given:
        def bookId = '1'
        def reviewEntity1 = new Review(
                id: 1L,
                book: new Book(id: bookId),
                user: new User(email: 'user1@example.com'),
                reviewText: 'Great book!'
        )
        def reviewEntity2 = new Review(
                id: 2L,
                book: new Book(id: bookId),
                user: new User(email: 'user2@example.com'),
                reviewText: 'Loved it!'
        )
        def reviewResponses = [
                new ReviewResponse(id: 1L,
                        bookId: bookId,
                        username: 'user1@example.com',
                        reviewText: 'Great book!'
                ),
                new ReviewResponse(
                        id: 2L,
                        bookId: bookId,
                        username: 'user2@example.com',
                        reviewText: 'Loved it!'
                )
        ]

        and:
        reviewRepository.findByBookId(bookId) >> [reviewEntity1, reviewEntity2]
        reviewMapper.toResponse(reviewEntity1) >> reviewResponses[0]
        reviewMapper.toResponse(reviewEntity2) >> reviewResponses[1]

        when:
        def result = reviewService.getReviewsByBookId(bookId)

        then:
        result == reviewResponses
    }

    def 'should create review successfully'() {
        given:
        def request = new CreateReviewRequest(
                bookId: '1',
                reviewText: 'Amazing book!'
        )
        def username = 'user1@example.com'
        def user = new User(email: username)
        def book = new Book(
                id: '1',
                title: 'Harry Potter'
        )
        def reviewEntity = new Review(
                id: 1L,
                book: book,
                user: user,
                reviewText: 'Amazing book!'
        )
        def reviewIdResponse = new ReviewIdResponse(reviewId: 1L)

        and:
        mockSecurityContext(username)
        userService.findByEmail(username) >> user
        bookService.getBookById(request.bookId) >> book
        reviewMapper.toEntity(request, user, book) >> reviewEntity
        reviewMapper.toIdResponse(reviewEntity) >> reviewIdResponse
        reviewRepository.save(reviewEntity) >> reviewEntity

        when:
        def result = reviewService.createReview(request)

        then:
        result == reviewIdResponse
    }

    def 'should throw BadRequestException if review already exists for book by user'() {
        given:
        def request = new CreateReviewRequest(
                bookId: '1',
                reviewText: 'Amazing book!',
        )
        def username = 'user1@example.com'
        def user = new User(email: username)
        def book = new Book(id: '1', title: 'Harry Potter')

        and:

        userService.findByEmail(username) >> user
        bookService.getBookById(request.bookId) >> book
        reviewRepository.existsByBookIdAndUserId(book.id, user.id) >> true

        when:
        reviewService.createReview(request)

        then:
        thrown(BadRequestException)
    }

    def 'should update review successfully'() {
        given:
        def reviewId = 1L
        def username = 'user1@example.com'
        def request = new UpdateReviewRequest(reviewText: 'Updated review content')
        def existingReview = new Review(
                id: reviewId,
                book: new Book(id: '1'),
                user: new User(email: username),
                reviewText: 'Old review'
        )
        def updatedReview = new Review(
                id: reviewId,
                book: new Book(id: '1'),
                user: new User(email: username),
                reviewText: 'Updated review content'
        )
        def reviewIdResponse = new ReviewIdResponse(reviewId: reviewId)

        and:
        mockSecurityContext(username)
        reviewRepository.findById(reviewId) >> Optional.of(existingReview)
        reviewRepository.save(updatedReview) >> updatedReview
        reviewMapper.toIdResponse(updatedReview) >> reviewIdResponse

        when:
        def result = reviewService.updateReview(reviewId, request)

        then:
        result == reviewIdResponse
        1 * reviewMapper.update(request, existingReview)
    }

    def 'should throw NotFoundException when review to update is not found'() {
        given:
        def reviewId = 1L
        def request = new UpdateReviewRequest(reviewText: 'Updated review content')

        and:
        reviewRepository.findById(reviewId) >> Optional.empty()

        when:
        reviewService.updateReview(reviewId, request)

        then:
        thrown(NotFoundException)
    }

    def 'should delete review successfully'() {
        given:
        def reviewId = 1L

        def username = 'user1@example.com'
        def existingReview = new Review(
                id: reviewId,
                book: new Book(id: '1'),
                user: new User(email: username),
                reviewText: 'Good book'
        )

        and:
        mockSecurityContext(username)
        reviewRepository.findById(reviewId) >> Optional.of(existingReview)

        when:
        reviewService.deleteReview(reviewId)

        then:
        1 * reviewRepository.delete(existingReview)
    }

    def 'should throw NotFoundException when review to delete is not found'() {
        given:
        def reviewId = 1L

        and:
        reviewRepository.findById(reviewId) >> Optional.empty()

        when:
        reviewService.deleteReview(reviewId)

        then:
        thrown(NotFoundException)
        0 * reviewRepository.delete(_)
    }

    private void mockSecurityContext(String username) {
        def authentication = Mock(Authentication) {
            getName() >> username
        }
        def securityContext = Mock(SecurityContext) {
            getAuthentication() >> authentication
        }
        SecurityContextHolder.setContext(securityContext)
    }

}
