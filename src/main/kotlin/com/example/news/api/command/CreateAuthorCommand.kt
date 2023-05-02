package com.example.news.api.command

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class CreateAuthorCommand(
    @field:NotBlank(message = "{author.fullName.notBlank}")
    @field:Size(max = 255, message = "{author.fullName.size}")
    val fullName: String,

    @field:Email(message = "{author.email}") val email: String,
)
