package com.example.news.repository

import com.example.news.domain.Author
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository

interface AuthorRepository : JpaRepository<Author, UUID> {
    fun existsByEmail(toEmailString: String): Boolean
}
