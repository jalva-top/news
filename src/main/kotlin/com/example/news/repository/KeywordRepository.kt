package com.example.news.repository

import com.example.news.domain.Keyword
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository

interface KeywordRepository : JpaRepository<Keyword, UUID> {
    fun findByValueIgnoreCase(keywordValue: String): Keyword?
    fun findAllByValueIn(value: Collection<String>): List<Keyword>
}
