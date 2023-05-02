package com.example.news.domain

import java.time.Instant
import java.util.UUID
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.Table

@Entity
@Table(name = "articles")
data class Article(
    @Id
    val id: UUID = UUID.randomUUID(),
    val header: String,

    @Column(name = "short_description")
    val shortDescription: String,
    val text: String,

    @ManyToMany
    @JoinTable(
        name = "article_authors",
        joinColumns = [JoinColumn(name = "article_id")],
        inverseJoinColumns = [JoinColumn(name = "author_id")]
    )
    val authors: List<Author> = listOf(),

    @ManyToMany(cascade = [CascadeType.ALL])
    @JoinTable(
        name = "article_keywords",
        joinColumns = [JoinColumn(name = "article_id")],
        inverseJoinColumns = [JoinColumn(name = "keyword_id")]
    )
    val keywords: List<Keyword> = listOf(),

    val publishedAt: Instant,
) {
    override fun toString() = "Article (id: $id)"
    override fun hashCode() = id.hashCode()
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true
        if (other !is Article) return false

        return id == other.id
    }
}
