package com.example.news.repository

import com.example.news.domain.User
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, UUID> {
    fun findByUsername(username: String): User?
    fun existsByUsername(username: String): Boolean
}
