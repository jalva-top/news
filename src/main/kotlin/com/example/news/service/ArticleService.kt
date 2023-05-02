package com.example.news.service

import com.example.news.api.command.CreateArticleCommand
import com.example.news.api.command.UpdateArticleCommand
import com.example.news.domain.Article
import com.example.news.domain.Author
import com.example.news.domain.Keyword
import com.example.news.exception.AlreadyExistException
import com.example.news.exception.NotFoundException
import com.example.news.repository.ArticleRepository
import java.time.Instant
import java.util.UUID
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
    private val authorService: AuthorService,
    private val keywordService: KeywordService,
) {
    companion object {
        const val NOT_FOUND_BY_ARTICLE_ID_MESSAGE = "No article found for given id"
    }

    @Transactional
    fun create(command: CreateArticleCommand): UUID {
        throwIfDuplicate(command)
        val authors = authorService.findAllAuthorsByIdsOrThrow(command.authors)
        val keywords = getKeywords(command.keywords)
        val newArticle = articleRepository.save(
            command.toEntity(authors, keywords)
        )

        return newArticle.id
    }

    @Transactional
    fun update(articleId: UUID, command: UpdateArticleCommand) {
        val article =
            articleRepository.findArticleById(articleId) ?: throw NotFoundException(NOT_FOUND_BY_ARTICLE_ID_MESSAGE)
        val authors = command.authors?.let { ids -> authorService.findAllAuthorsByIdsOrThrow(ids) }
        val keywords = command.keywords?.let { values -> getKeywords(values) }
        val updatedArticle = article.toUpdatedCopy(command, authors, keywords)

        articleRepository.save(updatedArticle)
    }

    fun delete(articleId: UUID) {
        if (articleRepository.existsById(articleId)) articleRepository.deleteById(articleId)
        else throw NotFoundException(NOT_FOUND_BY_ARTICLE_ID_MESSAGE)
    }

    private fun throwIfDuplicate(command: CreateArticleCommand) {
        val articleExists = articleRepository.existsByHeaderAndShortDescription(
            command.header, command.shortDescription
        )
        if (articleExists) {
            throw AlreadyExistException("Article with given header and short description already exists")
        }
    }

    private fun getKeywords(keywordValues: List<String>): List<Keyword> {
        val existedKeywordsMap = keywordService.findAllExistedKeywords(keywordValues).associateBy { it.value }
        return keywordValues.map { keywordValue ->
            existedKeywordsMap[keywordValue] ?: Keyword(value = keywordValue, articles = emptyList())
        }
    }

    private fun CreateArticleCommand.toEntity(
        authors: List<Author>,
        keywords: List<Keyword>,
    ) = Article(
        header = header,
        shortDescription = shortDescription,
        text = text,
        authors = authors,
        keywords = keywords,
        publishedAt = Instant.now()
    )

    private fun Article.toUpdatedCopy(
        command: UpdateArticleCommand,
        updatedAuthors: List<Author>?,
        updatedKeywords: List<Keyword>?,
    ) = copy(
        header = command.header ?: header,
        shortDescription = command.shortDescription ?: shortDescription,
        text = command.text ?: text,
        authors = updatedAuthors ?: authors,
        keywords = updatedKeywords ?: keywords,
    )
}
