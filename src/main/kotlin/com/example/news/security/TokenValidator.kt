package com.example.news.security

import org.springframework.security.core.userdetails.UserDetails

interface TokenValidator {
    fun isValidToken(token: String, userDetails: UserDetails): Boolean
}
