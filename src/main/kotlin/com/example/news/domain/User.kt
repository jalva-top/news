package com.example.news.domain

import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.Table
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = "users")
data class User(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "username")
    private val username: String,
    private val password: String,
    val active: Boolean = true,

    @Enumerated(EnumType.STRING)
    val role: UserRole,
) : UserDetails {
    override fun getUsername(): String = username
    override fun getPassword(): String = password
    override fun isEnabled(): Boolean = active
    override fun isCredentialsNonExpired(): Boolean = active
    override fun isAccountNonExpired(): Boolean = active
    override fun isAccountNonLocked(): Boolean = active
    override fun getAuthorities(): MutableCollection<GrantedAuthority> =
        AuthorityUtils.createAuthorityList(role.name)

    override fun toString() = "User (id: $id)"
    override fun hashCode() = id.hashCode()
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true
        if (other !is User) return false

        return id == other.id
    }
}

enum class UserRole { VIEWER, EDITOR }
