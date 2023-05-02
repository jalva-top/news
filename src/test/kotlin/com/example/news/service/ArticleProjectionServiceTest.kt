package com.example.news.service

import com.example.news.api.common.PaginationDetails
import com.example.news.api.common.PaginationRequest
import com.example.news.api.response.ArticleDto
import com.example.news.api.response.AuthorDto
import com.example.news.domain.Article
import com.example.news.domain.Author
import com.example.news.domain.Keyword
import com.example.news.repository.ArticleRepository
import com.example.news.repository.AuthorRepository
import com.example.news.repository.KeywordRepository
import io.mockk.every
import io.mockk.mockk
import java.time.Instant
import java.util.Optional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

internal class ArticleProjectionServiceTest {
    private val articleRepository: ArticleRepository = mockk()
    private val authorRepository: AuthorRepository = mockk()
    private val keywordRepository: KeywordRepository = mockk()
    private val service = ArticleProjectionService(
        articleRepository = articleRepository,
        authorRepository = authorRepository,
        keywordRepository = keywordRepository,
    )

    companion object {
        private val author1 = Author(fullName = "John Doe", email = "john.doe@example.com")
        private val author2 = Author(fullName = "Olivia Smith", email = "olivia.smith@example.com")
        private val keyword1 = Keyword(value = "Memes")
        private val keyword2 = Keyword(value = "Oscars 2023")
        private val article = Article(
            header = "Header",
            shortDescription = "Short description",
            text = "Text",
            authors = listOf(author1, author2),
            keywords = listOf(keyword1, keyword2),
            publishedAt = Instant.parse("2023-05-01T14:56:00Z"),
        )
        private val expectedArticleDto = ArticleDto(
            articleId = article.id,
            header = article.header,
            shortDescription = article.shortDescription,
            text = article.text,
            authors = listOf(
                AuthorDto(authorId = author1.id, fullName = author1.fullName),
                AuthorDto(authorId = author2.id, fullName = author2.fullName),
            ),
            keywords = listOf(keyword1.value, keyword2.value),
            publishedAt = article.publishedAt,
        )
        private val paginationRequest = PaginationRequest(2, 10)
        private val pageable = PageRequest.of(
            paginationRequest.page, paginationRequest.pageSize, Sort.Direction.DESC, "id"
        )
        private val page: Page<Article> = PageImpl(
            listOf(article), pageable, 21
        )
        private val expectedPaginationDetails = PaginationDetails(
            page = paginationRequest.page,
            pageSize = 1,
            totalElements = page.totalElements
        )
    }

    @Test
    fun `should return article dtos by author`() {
        // Given
        every { authorRepository.findById(author1.id) } returns Optional.of(author1)
        every { articleRepository.findAllByAuthor(author1, pageable) } returns page

        // When
        val response = service.findAllByAuthorId(author1.id, paginationRequest)

        // Then
        assertThat(response.content).usingRecursiveComparison()
            .isEqualTo(listOf(expectedArticleDto))

        assertThat(response.pagination).usingRecursiveComparison()
            .isEqualTo(expectedPaginationDetails)
    }

    @Test
    fun `should return article dtos by keyword`() {
        // Given
        every { keywordRepository.findByValueIgnoreCase(keyword2.value) } returns keyword2
        every { articleRepository.findAllByKeyword(keyword2, pageable) } returns page

        // When
        val response = service.findAllByKeyword(keyword2.value, paginationRequest)

        // Then
        assertThat(response.content).usingRecursiveComparison()
            .isEqualTo(listOf(expectedArticleDto))

        assertThat(response.pagination).usingRecursiveComparison()
            .isEqualTo(expectedPaginationDetails)
    }

    @Test
    fun `should return article dtos by period`() {
        // Given
        val from = Instant.now().minusSeconds(600)
        val to = Instant.now().minusSeconds(100)
        every { articleRepository.findAllByPublishedAtBetween(from, to, pageable) } returns page

        // When
        val response = service.findAllByPeriod(from, to, paginationRequest)

        // Then
        assertThat(response.content).usingRecursiveComparison()
            .isEqualTo(listOf(expectedArticleDto))

        assertThat(response.pagination).usingRecursiveComparison()
            .isEqualTo(expectedPaginationDetails)
    }

    @Test
    fun `should throw on invalid period`() {
        // Given
        val from = Instant.now().minusSeconds(600)
        val to = Instant.now().minusSeconds(1000_000)
        val expectedMessage = "Period start date have to be before end date"

        // When
        val exception = assertThrows<java.lang.IllegalArgumentException> {
            service.findAllByPeriod(from, to, paginationRequest)
        }

        // Then
        assertEquals(expectedMessage, exception.message)
    }

    @Test
    fun `should return article dto by id`() {
        // Given
        every { articleRepository.findArticleById(article.id) } returns article

        // When
        val actualDto = service.findById(article.id)

        // Then
        assertThat(actualDto).usingRecursiveComparison()
            .isEqualTo(expectedArticleDto)
    }
}