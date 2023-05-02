package com.example.news.domain

import java.util.UUID
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "tokens")
data class Token(
    @Id
    val id: UUID = UUID.randomUUID(),
    private val value: String,
    val revoked: Boolean = false,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,
) {
    override fun toString() = "Token (id: $id)"
    override fun hashCode() = id.hashCode()
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true
        if (other !is Token) return false

        return id == other.id
    }
}
