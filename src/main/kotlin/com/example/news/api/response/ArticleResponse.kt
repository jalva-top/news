package com.example.news.api.response

import java.time.Instant
import java.util.UUID

data class ArticleDto(
    val articleId: UUID,
    val header: String,
    val shortDescription: String,
    val text: String,
    val authors: List<AuthorDto>,
    val keywords: List<String>,
    val publishedAt: Instant,
)

data class AuthorDto(
    val authorId: UUID,
    val fullName: String,
)
