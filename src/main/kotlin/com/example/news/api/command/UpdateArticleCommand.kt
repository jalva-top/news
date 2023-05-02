package com.example.news.api.command

import com.example.news.api.validation.ValidKeywords
import java.util.UUID
import javax.validation.constraints.Size

data class UpdateArticleCommand(
    @field:Size(min = 1, max = 255, message = "{article.header.size}")
    val header: String? = null,

    @field:Size(min = 1, max = 1023, message = "{article.shortDescription.size}")
    val shortDescription: String? = null,

    val text: String? = null,
    val authors: List<UUID>? = null,

    @field:ValidKeywords
    val keywords: List<String>? = null,
)
