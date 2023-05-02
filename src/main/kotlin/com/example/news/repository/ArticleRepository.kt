package com.example.news.repository

import com.example.news.domain.Article
import com.example.news.domain.Author
import com.example.news.domain.Keyword
import java.time.Instant
import java.util.UUID
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ArticleRepository : JpaRepository<Article, UUID> {

    fun findArticleById(id: UUID): Article?
    fun existsByHeaderAndShortDescription(header: String, shortDescription: String): Boolean

    @Query("from Article a where :author member of a.authors")
    fun findAllByAuthor(author: Author, pageable: Pageable): Page<Article>

    @Query("from Article a where :keyword member of a.keywords")
    fun findAllByKeyword(keyword: Keyword, pageable: Pageable): Page<Article>

    fun findAllByPublishedAtBetween(from: Instant, to: Instant, pageable: Pageable): Page<Article>
}
