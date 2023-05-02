package com.example.news.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import java.util.Date
import javax.crypto.SecretKey
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service


@Service
class JwtService(
    @Value("\${application.jwt.secret-key}") private val secretKey: String,
    @Value("\${application.jwt.expiration}") private val jwtExpiration: Long,
) : TokenValidator {
    fun generateToken(
        userDetails: UserDetails,
        additionalClaims: Map<String, String> = emptyMap(),
    ): String = generateToken(additionalClaims, userDetails, jwtExpiration)

    fun extractUsername(token: String): String = extractClaims(token).subject

    override fun isValidToken(token: String, userDetails: UserDetails): Boolean {
        val tokenUsername = extractUsername(token)
        return tokenUsername == userDetails.username && !isExpired(token)
    }

    private fun generateToken(
        additionalClaims: Map<String, Any>,
        userDetails: UserDetails,
        expiration: Long,
    ): String = Jwts.builder()
        .setClaims(additionalClaims)
        .setSubject(userDetails.username)
        .setIssuedAt(Date(System.currentTimeMillis()))
        .setExpiration(Date(System.currentTimeMillis() + expiration))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact()
    private fun extractClaims(token: String): Claims =
        Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .body


    private fun isExpired(token: String) = extractExpiration(token).before(Date())

    private fun extractExpiration(token: String): Date = extractClaims(token).expiration

    private fun getSigningKey(): SecretKey {
        val keyBytes = Decoders.BASE64.decode(secretKey)
        return Keys.hmacShaKeyFor(keyBytes)
    }
}