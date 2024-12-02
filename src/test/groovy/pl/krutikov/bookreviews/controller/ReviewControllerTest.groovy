package pl.krutikov.bookreviews.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import pl.krutikov.bookreviews.dto.request.CreateReviewRequest
import pl.krutikov.bookreviews.dto.request.UpdateReviewRequest
import pl.krutikov.bookreviews.dto.response.ReviewIdResponse
import pl.krutikov.bookreviews.dto.response.ReviewResponse
import pl.krutikov.bookreviews.exceptionhandler.GlobalExceptionHandler
import pl.krutikov.bookreviews.service.ReviewService
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import java.sql.Timestamp

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete

class ReviewControllerTest extends Specification {

    GlobalExceptionHandler handler
    ObjectMapper objectMapper
    ReviewService reviewService
    MockMvc mockMvc

    @Subject
    ReviewController reviewController

    def url = '/api/v1/reviews'

    def setup() {
        handler = new GlobalExceptionHandler()
        objectMapper = new ObjectMapper()
        reviewService = Mock()
        reviewController = new ReviewController(reviewService)
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController)
                .setControllerAdvice(handler)
                .build()
    }

    def 'should get reviews by book ID successfully'() {
        given:
        def bookId = '123'
        def timestamp = Timestamp.valueOf('2024-11-01 10:00:00')
        def reviews = [
                new ReviewResponse(
                        id: 1L,
                        userId: 100L,
                        username: 'User_1',
                        bookId: bookId,
                        reviewText: 'Excellent book!',
                        createdAt: timestamp,
                        updatedAt: timestamp
                ),
                new ReviewResponse(
                        id: 2L,
                        userId: 101L,
                        username: 'User_2',
                        bookId: bookId,
                        reviewText: 'Did not like it.',
                        createdAt: timestamp,
                        updatedAt: timestamp
                )
        ]

        and:
        reviewService.getReviewsByBookId(bookId) >> reviews

        when:
        def result = mockMvc.perform(get("$url/book/$bookId")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn()

        then:
        result.response.status == HttpStatus.OK.value()
        result.response.contentAsString == objectMapper.writeValueAsString(reviews)
    }

    def 'should create a review successfully'() {
        given:
        def createRequest = new CreateReviewRequest(
                bookId: '123',
                reviewText: 'Great book!'
        )
        def createResponse = new ReviewIdResponse(reviewId: 1L)

        and:
        reviewService.createReview(createRequest) >> createResponse

        when:
        def result = mockMvc.perform(post(url)
                .content(objectMapper.writeValueAsString(createRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn()

        then:
        result.response.status == HttpStatus.OK.value()
        result.response.contentAsString == objectMapper.writeValueAsString(createResponse)
    }

    def 'should update a review successfully'() {
        given:
        def reviewId = 1L
        def updateRequest = new UpdateReviewRequest(reviewText: 'Updated review text')
        def updateResponse = new ReviewIdResponse(reviewId: reviewId)

        and:
        reviewService.updateReview(reviewId, updateRequest) >> updateResponse

        when:
        def result = mockMvc.perform(put("$url/$reviewId")
                .content(objectMapper.writeValueAsString(updateRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn()

        then:
        result.response.status == HttpStatus.OK.value()
        result.response.contentAsString == objectMapper.writeValueAsString(updateResponse)
    }

    @Unroll
    def 'should return 400 when invalid input for update review: #description'() {
        given:
        def reviewId = 1L
        def updateRequest = new UpdateReviewRequest(reviewText: reviewText)

        when:
        def result = mockMvc.perform(put("$url/$reviewId")
                .content(objectMapper.writeValueAsString(updateRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn()

        then:
        0 * reviewService.updateReview(reviewId, updateRequest)
        result.response.status == HttpStatus.BAD_REQUEST.value()

        where:
        reviewText | description
        null       | 'reviewText is null'
        '   '      | 'reviewText is empty'
    }

    def 'should delete a review successfully'() {
        given:
        def reviewId = 1L

        when:
        def result = mockMvc.perform(delete("$url/$reviewId")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn()

        then:
        1 * reviewService.deleteReview(reviewId)
        result.response.status == HttpStatus.OK.value()
    }

    @Unroll
    def 'should return 400 when invalid input for create review: #description'() {
        given:
        def createRequest = new CreateReviewRequest(bookId: bookId, reviewText: reviewText)

        when:
        def result = mockMvc.perform(post(url)
                .content(objectMapper.writeValueAsString(createRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn()

        then:
        0 * reviewService.createReview(createRequest)
        result.response.status == HttpStatus.BAD_REQUEST.value()

        where:
        bookId | reviewText | description
        null   | 'Great!'   | 'bookId is null'
        '   '  | 'Great!'   | 'bookId is empty'
        '123'  | null       | 'reviewText is null'
        '123'  | '   '      | 'reviewText is empty'
    }

}
