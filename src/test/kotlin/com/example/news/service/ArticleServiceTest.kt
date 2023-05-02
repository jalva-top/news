package com.example.news.service

import com.example.news.api.command.CreateArticleCommand
import com.example.news.api.command.UpdateArticleCommand
import com.example.news.domain.Article
import com.example.news.domain.Author
import com.example.news.domain.Keyword
import com.example.news.exception.AlreadyExistException
import com.example.news.repository.ArticleRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.time.Instant
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ArticleServiceTest {
    private val articleRepository: ArticleRepository = mockk()
    private val authorService: AuthorService = mockk()
    private val keywordService: KeywordService = mockk()

    private val service = ArticleService(
        articleRepository = articleRepository,
        authorService = authorService,
        keywordService = keywordService,
    )

    companion object {
        private val author1 = Author(fullName = "John Doe", email = "john.doe@example.com")
        private val author2 = Author(fullName = "Olivia Smith", email = "olivia.smith@example.com")
        private val keyword1 = Keyword(value = "Memes")
        private val keyword2 = Keyword(value = "Oscars 2023")

        private val createCommand = CreateArticleCommand(
            header = "Header",
            shortDescription = "Short description",
            text = "Text",
            authors = listOf(author1.id, author2.id),
            keywords = listOf(keyword1.value, keyword2.value),
        )
    }

    @Test
    fun `should save an article as expected`() {
        // Given
        val expectedArticleTemplate = Article(
            header = createCommand.header,
            shortDescription = createCommand.shortDescription,
            text = createCommand.text,
            authors = listOf(author1, author2),
            keywords = listOf(keyword1, keyword2),
            publishedAt = Instant.now(),
        )

        // And - mocks
        every {
            articleRepository.existsByHeaderAndShortDescription(
                header = createCommand.header,
                shortDescription = createCommand.shortDescription
            )
        } returns false
        every {
            authorService.findAllAuthorsByIdsOrThrow(createCommand.authors)
        } returns listOf(author1, author2)
        every {
            keywordService.findAllExistedKeywords(createCommand.keywords)
        } returns listOf(keyword1, keyword2)
        val articleSlot = slot<Article>()
        every {
            articleRepository.save(capture(articleSlot))
        } returnsArgument (0)

        val testStartDate = Instant.now()

        // When
        val actualArticleId = service.create(createCommand)

        // Then - method returns expected result
        assertTrue(articleSlot.isCaptured, "Article was not persisted")
        val persistedArticle = articleSlot.captured
        assertEquals(persistedArticle.id, actualArticleId)

        // And - publish date set as expected
        assertThat(persistedArticle.publishedAt)
            .isAfterOrEqualTo(testStartDate)
            .isBeforeOrEqualTo(Instant.now())

        // And
        val expectedArticle = expectedArticleTemplate.copy(
            id = persistedArticle.id, publishedAt = persistedArticle.publishedAt
        )
        assertThat(persistedArticle).usingRecursiveComparison()
            .isEqualTo(expectedArticle)
    }

    @Test
    fun `should throw an exception when an article with given header and short description already exists`() {
        // Given
        val expectedExceptionMessage = "Article with given header and short description already exists"
        every {
            articleRepository.existsByHeaderAndShortDescription(
                header = createCommand.header,
                shortDescription = createCommand.shortDescription
            )
        } returns true

        // When
        val exception = assertThrows<AlreadyExistException> {
            service.create(createCommand)
        }

        // Then
        assertEquals(expectedExceptionMessage, exception.message)
    }

    @Test
    fun `should update an article as expected`() {
        // Given
        val article = Article(
            header = "Header",
            shortDescription = "Short description",
            text = "Text",
            authors = listOf(author1, author2),
            keywords = listOf(keyword1, keyword2),
            publishedAt = Instant.parse("2023-05-01T14:56:00Z"),
        )
        val newAuthor = Author(fullName = "New Author", email = "new@example.com")
        val newKeyword = Keyword(value = "New keyword")
        val updateCommand = UpdateArticleCommand(
            header = "New header",
            shortDescription = "New short description",
            text = "New text",
            authors = listOf(newAuthor.id),
            keywords = listOf(newKeyword.value, keyword1.value),
        )
        val expectedUpdatedArticle = article.copy(
            header = updateCommand.header!!,
            shortDescription = updateCommand.shortDescription!!,
            text = updateCommand.text!!,
            authors = listOf(newAuthor),
            keywords = listOf(newKeyword, keyword1),
        )

        // And - mocks
        every {
            articleRepository.findArticleById(article.id)
        } returns article
        every {
            keywordService.findAllExistedKeywords(updateCommand.keywords!!)
        } returns listOf(keyword1, newKeyword)
        every {
            authorService.findAllAuthorsByIdsOrThrow(updateCommand.authors!!)
        } returns listOf(newAuthor)
        val articleSlot = slot<Article>()
        every {
            articleRepository.save(capture(articleSlot))
        } returnsArgument (0)

        // When
        service.update(articleId = article.id, command = updateCommand)

        // Then
        assertTrue(articleSlot.isCaptured, "Article was not updated")
        val updatedArticle = articleSlot.captured

        assertThat(updatedArticle).usingRecursiveComparison()
            .isEqualTo(expectedUpdatedArticle)
    }

    @Test
    fun `should update nothing when update command no fields to update`() {
        // Given
        val article = Article(
            header = "Header",
            shortDescription = "Short description",
            text = "Text",
            authors = listOf(author1, author2),
            keywords = listOf(keyword1, keyword2),
            publishedAt = Instant.parse("2023-05-01T14:56:00Z"),
        )
        val updateCommand = UpdateArticleCommand()

        // And - mocks
        every {
            articleRepository.findArticleById(article.id)
        } returns article
        val articleSlot = slot<Article>()
        every {
            articleRepository.save(capture(articleSlot))
        } returnsArgument (0)

        // When
        service.update(articleId = article.id, command = updateCommand)

        // Then
        verify(exactly = 1) { articleRepository.save(article) }
        assertThat(articleSlot.captured).usingRecursiveComparison()
            .isEqualTo(article)
    }



    @Test
    fun `should update fields presents in update command only`() {
        // Given
        val article = Article(
            header = "Header",
            shortDescription = "Short description",
            text = "Text",
            authors = listOf(author1, author2),
            keywords = listOf(keyword1, keyword2),
            publishedAt = Instant.parse("2023-05-01T14:56:00Z"),
        )
        val updateCommand = UpdateArticleCommand(header = "New header", text = "New text")
        val expectedArticle = article.copy(header = updateCommand.header!!, text = updateCommand.text!!)

        // And - mocks
        every {
            articleRepository.findArticleById(article.id)
        } returns article
        val articleSlot = slot<Article>()
        every {
            articleRepository.save(capture(articleSlot))
        } returnsArgument (0)

        // When
        service.update(articleId = article.id, command = updateCommand)

        // Then
        verify(exactly = 1) { articleRepository.save(article) }
        assertThat(articleSlot.captured).usingRecursiveComparison()
            .isEqualTo(expectedArticle)
    }
}