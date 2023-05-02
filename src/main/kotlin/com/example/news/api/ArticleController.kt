package com.example.news.api

import com.example.news.api.command.CreateArticleCommand
import com.example.news.api.command.UpdateArticleCommand
import com.example.news.api.common.PaginationRequest
import com.example.news.api.response.CreateArticleCommandResponse
import com.example.news.service.ArticleProjectionService
import com.example.news.service.ArticleService
import java.time.Instant
import java.util.UUID
import javax.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/articles")
class ArticleController(
    private val articleService: ArticleService,
    private val articleProjectionService: ArticleProjectionService,
) {
    @PostMapping
    fun createArticle(@Valid @RequestBody command: CreateArticleCommand): ResponseEntity<CreateArticleCommandResponse> {
        val articleId = articleService.create(command)

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(CreateArticleCommandResponse(articleId))
    }

    @PutMapping("{articleId}")
    fun updateArticle(
        @PathVariable articleId: UUID,
        @Valid @RequestBody command: UpdateArticleCommand
    ): ResponseEntity<Void> {
        articleService.update(articleId, command)

        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("{articleId}")
    fun deleteArticle(@PathVariable articleId: UUID) = articleService.delete(articleId)

    @GetMapping("{articleId}")
    fun getArticleById(@PathVariable articleId: UUID) = articleProjectionService.findById(articleId)

    @GetMapping("author/{authorId}")
    fun getArticlesByAuthor(
        @PathVariable authorId: UUID,
        @Valid pagingRequest: PaginationRequest,
    ) = articleProjectionService.findAllByAuthorId(authorId, pagingRequest)

    @GetMapping("keyword/{keyword}")
    fun getArticlesByKeyword(
        @PathVariable keyword: String,
        @Valid pagingRequest: PaginationRequest,
    ) = articleProjectionService.findAllByKeyword(keyword, pagingRequest)

    @GetMapping
    fun getArticlesByUtcPeriod(
        @RequestParam from: Instant,
        @RequestParam to: Instant = Instant.now(),
        @Valid pagingRequest: PaginationRequest,
    ) = articleProjectionService.findAllByPeriod(from, to, pagingRequest)
}
