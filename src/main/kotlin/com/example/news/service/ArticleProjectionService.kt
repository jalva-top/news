package com.example.news.service

import com.example.news.api.common.PaginationDetails
import com.example.news.api.common.PaginationRequest
import com.example.news.api.common.PaginationResponse
import com.example.news.api.response.ArticleDto
import com.example.news.api.response.AuthorDto
import com.example.news.domain.Article
import com.example.news.exception.NotFoundException
import com.example.news.repository.ArticleRepository
import com.example.news.repository.AuthorRepository
import com.example.news.repository.KeywordRepository
import java.time.Instant
import java.util.UUID
import javax.persistence.EntityNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ArticleProjectionService(
    private val articleRepository: ArticleRepository,
    private val authorRepository: AuthorRepository,
    private val keywordRepository: KeywordRepository,
) {

    @Transactional(readOnly = true)
    fun findById(articleId: UUID): ArticleDto = articleRepository.findArticleById(articleId)?.toDto()
        ?: throw NotFoundException("Article with given id was not found")


    @Transactional(readOnly = true)
    fun findAllByAuthorId(authorId: UUID, pagingRequest: PaginationRequest): PaginationResponse<ArticleDto> {
        val author = authorRepository.findById(authorId)
            .orElseThrow { NotFoundException("Author for give id was not found") }

        val page = articleRepository.findAllByAuthor(author, pagingRequest.toPageable())

        return page.toPagingResponse()
    }

    @Transactional(readOnly = true)
    fun findAllByKeyword(keywordValue: String, pagingRequest: PaginationRequest): PaginationResponse<ArticleDto> {
        val keyword =
            keywordRepository.findByValueIgnoreCase(keywordValue) ?: throw NotFoundException("Keyword was not found")
        val page = articleRepository.findAllByKeyword(keyword, pagingRequest.toPageable())

        return page.toPagingResponse()
    }

    @Transactional(readOnly = true)
    fun findAllByPeriod(from: Instant, to: Instant, pagingRequest: PaginationRequest): PaginationResponse<ArticleDto> {
        require(from.isBefore(to)) {
            "Period start date have to be before end date"
        }

        val page = articleRepository.findAllByPublishedAtBetween(from, to, pagingRequest.toPageable())

        return page.toPagingResponse()
    }


    private fun Page<Article>.toPagingResponse() = PaginationResponse(
        content.map { it.toDto() }, toPagingDetails()
    )

    private fun Page<*>.toPagingDetails() =
        PaginationDetails(page = number, pageSize = content.size, totalElements = totalElements)

    private fun PaginationRequest.toPageable() = PageRequest.of(page, pageSize, Sort.Direction.DESC, "id")

    private fun Article.toDto(): ArticleDto = ArticleDto(
        articleId = id,
        header = header,
        shortDescription = shortDescription,
        text = text,
        authors = authors.map {
            AuthorDto(
                authorId = it.id,
                fullName = it.fullName
            )
        },
        keywords = keywords.map { it.value },
        publishedAt = publishedAt
    )
}
