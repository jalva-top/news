package com.example.news.api.command

import com.example.news.api.validation.ValidKeywords
import java.util.UUID
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size

data class CreateArticleCommand(
    @field:NotBlank(message = "{article.header.notBlank}")
    @field:Size(max = 255, message = "{article.header.size}")
    val header: String,

    @field:NotBlank(message = "{article.shortDescription.notBlank}")
    @field:Size(max = 1023, message = "{article.shortDescription.size}")
    val shortDescription: String,

    @field:NotBlank(message = "{article.text.notBlank}") val text: String,
    @field:NotEmpty(message = "{article.authors.notEmpty}") val authors: List<UUID>,

    @field:NotEmpty(message = "{article.keywords.notEmpty}")
    @field:ValidKeywords
    val keywords: List<String>,
)
