package com.example.news.repository

import com.example.news.domain.Token
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface TokenRepository : JpaRepository<Token, UUID> {
    @Query(
        "select t from Token t join t.user u where t.value=:value and t.revoked=false and u.username=:username"
    )
    fun findActiveTokenByValueAndUsername(value: String, username: String): Token?
}
