package com.example.news.api

import com.example.news.api.command.CreateAuthorCommand
import com.example.news.api.response.CreateAuthorCommandResponse
import com.example.news.service.AuthorService
import javax.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/authors")
class AuthorController(
    private val authorService: AuthorService,
) {
    @PostMapping
    fun createAuthor(@Valid @RequestBody command: CreateAuthorCommand): ResponseEntity<CreateAuthorCommandResponse> {
        val authorId = authorService.create(command)

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(CreateAuthorCommandResponse(authorId = authorId))
    }
}
