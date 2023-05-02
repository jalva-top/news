package com.example.news.domain

import java.util.UUID
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToMany
import javax.persistence.Table

@Entity
@Table(name = "keywords")
class Keyword(
    @Id
    val id: UUID = UUID.randomUUID(),
    val value: String,

    @ManyToMany(mappedBy = "keywords") val articles: List<Article> = listOf(),
) {
    override fun toString() = "Keyword (id: $id)"
    override fun hashCode() = id.hashCode()
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true
        if (other !is Keyword) return false

        return id == other.id
    }
}
