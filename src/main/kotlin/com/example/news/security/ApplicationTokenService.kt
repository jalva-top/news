package com.example.news.security

import com.example.news.domain.Token
import com.example.news.repository.TokenRepository
import java.util.UUID
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ApplicationTokenService(
    private val tokenRepository: TokenRepository,
    private val jwtService: JwtService,
    private val userDetailsService: UserDetailsServiceImpl,
) : TokenValidator {
    override fun isValidToken(token: String, userDetails: UserDetails): Boolean {
        val activeToken = tokenRepository.findActiveTokenByValueAndUsername(token, userDetails.username)

        return activeToken != null
    }

    @Transactional
    fun createToken(userId: UUID): String {
        val user = userDetailsService.findByIdOrThrow(userId)
        val value = jwtService.generateToken(user)
        val token = Token(value = value, user = user)
        tokenRepository.save(token)

        return value
    }
}
