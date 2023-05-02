package com.example.news.security

import com.example.news.api.command.CreateUserCommand
import com.example.news.domain.User
import com.example.news.exception.AlreadyExistException
import com.example.news.repository.UserRepository
import java.util.UUID
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) : UserDetailsService {
    override fun loadUserByUsername(username: String?): UserDetails =
        username?.let { userRepository.findByUsername(username) } ?: throw UsernameNotFoundException("User not found")

    fun findByIdOrThrow(userId: UUID): User =
        userRepository.findById(userId).orElseThrow { throw UsernameNotFoundException("User not found") }

    fun createUser(command: CreateUserCommand): UUID {
        val userExists = userRepository.existsByUsername(command.username)
        if (userExists) {
            throw AlreadyExistException("User with given username already exists")
        }
        val user = userRepository.save(command.toEntity())

        return user.id
    }

    private fun CreateUserCommand.toEntity() = User(
        username = username,
        password = passwordEncoder.encode(password),
        role = role,
    )
}
