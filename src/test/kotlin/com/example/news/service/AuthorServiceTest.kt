package com.example.news.service

import com.example.news.api.command.CreateAuthorCommand
import com.example.news.domain.Author
import com.example.news.exception.AlreadyExistException
import com.example.news.exception.NotFoundException
import com.example.news.repository.AuthorRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import java.util.UUID
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class AuthorServiceTest {
    private val authorRepository: AuthorRepository = mockk()
    private val service = AuthorService(authorRepository)

    @Test
    fun `should save an article as expected`() {
        // Given
        val email = "eXamPle@example.COM"
        val wellFormedEmail = "example@example.com"
        val command = CreateAuthorCommand(fullName = "Nassim Nicholas Taleb", email = email)
        val expectedAuthorTemplate = Author(fullName = command.fullName, email = wellFormedEmail)

        every {
            authorRepository.existsByEmail(wellFormedEmail)
        } returns false
        val authorSlot = slot<Author>()
        every {
            authorRepository.save(capture(authorSlot))
        } returnsArgument (0)

        // When
        val actualAuthorId: UUID = service.create(command)

        // Then
        assertTrue(authorSlot.isCaptured, "The author was not persisted")
        val persistedAuthor = authorSlot.captured
        assertEquals(persistedAuthor.id, actualAuthorId)

        // And
        val expectedAuthor = expectedAuthorTemplate.copy(id = actualAuthorId)
        assertThat(persistedAuthor).usingRecursiveComparison()
            .isEqualTo(expectedAuthor)
    }

    @Test
    fun `should throw an exception when an author with the given email already exists`() {
        // Given
        val email = "example@example.com"
        val command = CreateAuthorCommand(fullName = "Nassim Nicholas Taleb", email = email)
        val expectedExceptionMessage = "Author with given email address already exists"

        every {
            authorRepository.existsByEmail(email)
        } returns true

        // When
        val exception = assertThrows<AlreadyExistException> {
            service.create(command)
        }

        // Then
        assertEquals(expectedExceptionMessage, exception.message)
    }

    @Test
    fun `should throw an exception when some of the authors cannot be found`() {
        // Given
        val author1 = Author(fullName = "John Doe", email = "john.doe@example.com")
        val wrongAuthorId = UUID.randomUUID()
        val authorIds = listOf(author1.id, wrongAuthorId)
        val expectedExceptionMessage = "No authors found for the following IDs: [$wrongAuthorId]"

        every {
            authorRepository.findAllById(authorIds)
        } returns listOf(author1)

        // When
        val exception = assertThrows<NotFoundException> {
            service.findAllAuthorsByIdsOrThrow(authorIds)
        }

        // Then
        assertEquals(expectedExceptionMessage, exception.message)
    }
}
