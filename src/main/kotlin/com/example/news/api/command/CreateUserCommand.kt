package com.example.news.api.command

import com.example.news.domain.UserRole
import javax.validation.constraints.Size

data class CreateUserCommand(
    @field:Size(max = 255, message = "{user.username.size}")
    val username: String,
    val password: String,
    val role: UserRole,
)
