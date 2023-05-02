package com.example.news.service

import com.example.news.api.command.CreateAuthorCommand
import com.example.news.domain.Author
import com.example.news.exception.AlreadyExistException
import com.example.news.exception.NotFoundException
import com.example.news.repository.AuthorRepository
import java.util.UUID
import org.springframework.stereotype.Service

@Service
class AuthorService(
    private val authorRepository: AuthorRepository,
) {
    fun findAllAuthorsByIdsOrThrow(ids: Collection<UUID>): List<Author> {
        val authors = authorRepository.findAllById(ids)
        val foundAuthorsIds = authors.map { it.id }

        val notFoundAuthors = ids.filterNot { foundAuthorsIds.contains(it) }
        notFoundAuthors.takeIf { it.isNotEmpty() }?.let {
            throw NotFoundException("No authors found for the following IDs: $notFoundAuthors")
        }
        return authors
    }

    fun create(command: CreateAuthorCommand): UUID {
        val authorExists = authorRepository.existsByEmail(command.email.toEmailString())
        if (authorExists) {
            throw AlreadyExistException("Author with given email address already exists")
        }

        val newAuthor = authorRepository.save(command.toEntity())
        return newAuthor.id
    }

    private fun CreateAuthorCommand.toEntity() = Author(
        fullName = fullName,
        email = email.toEmailString(),
        articles = emptyList(),
    )

    private fun String.toEmailString() = trim().lowercase()
}
