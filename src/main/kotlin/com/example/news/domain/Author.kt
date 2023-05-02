package com.example.news.domain

import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToMany
import javax.persistence.Table

@Entity
@Table(name = "authors")
data class Author(
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column(name = "full_name") val fullName: String,
    val email: String,
    @ManyToMany(mappedBy = "authors") val articles: List<Article> = listOf(),
) {
    override fun toString() = "Author (id: $id)"
    override fun hashCode() = id.hashCode()
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true
        if (other !is Author) return false

        return id == other.id
    }
}
