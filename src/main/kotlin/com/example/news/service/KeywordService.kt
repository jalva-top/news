package com.example.news.service

import com.example.news.repository.KeywordRepository
import org.springframework.stereotype.Service

@Service
class KeywordService(
    private val keywordRepository: KeywordRepository,
) {
    fun findAllExistedKeywords(keywordValues: Collection<String>) = keywordRepository.findAllByValueIn(keywordValues)
}
