package com.example.news.api

import com.example.news.api.command.CreateUserCommand
import com.example.news.api.response.CreateUserCommandResponse
import com.example.news.api.response.TokenResponse
import com.example.news.security.ApplicationTokenService
import com.example.news.security.UserDetailsServiceImpl
import java.util.UUID
import javax.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/security/users/")
class SecurityController(
    private val userDetailsService: UserDetailsServiceImpl,
    private val applicationTokenService: ApplicationTokenService,
) {
    @PostMapping
    fun createUser(@Valid @RequestBody command: CreateUserCommand): ResponseEntity<CreateUserCommandResponse> {
        val userId = userDetailsService.createUser(command)

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(CreateUserCommandResponse(userId = userId))
    }

    @PostMapping("{userId}/token")
    fun createToken(@PathVariable userId: UUID): ResponseEntity<TokenResponse> {
        val token = applicationTokenService.createToken(userId)

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(TokenResponse(token))
    }
}
