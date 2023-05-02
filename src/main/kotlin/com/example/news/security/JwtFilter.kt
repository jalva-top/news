package com.example.news.security

import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter


@Component
class JwtFilter(
    private val jwtService: JwtService,
    private val applicationTokenService: ApplicationTokenService,
    private val userDetailsService: UserDetailsService,
) : OncePerRequestFilter() {
    companion object {
        const val AUTHORIZATION = "Authorization"
        const val BEARER_PREFIX = "Bearer "
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        if (request.haveToBeIgnored()) {
            filterChain.doFilter(request, response)
            return
        }

        val jwt: String? = request.findJwtIfPresent()
        SecurityContextHolder.getContext().authentication = jwt?.let { toAuthentication(it, request) }
        filterChain.doFilter(request, response)
    }

    private fun HttpServletRequest.haveToBeIgnored(): Boolean = servletPath.contains("/security/users") ||
            (method == HttpMethod.GET.name && servletPath.contains("api/v1/articles/"))

    private fun toAuthentication(jwt: String, request: HttpServletRequest): Authentication? {
        val username = jwtService.extractUsername(jwt)
        val userDetails = userDetailsService.loadUserByUsername(username)
        if (isValidToken(jwt, userDetails).not()) {
            return null
        }

        val authenticationToken = UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.authorities
        ).apply {
            details = WebAuthenticationDetailsSource().buildDetails(request)
        }
        return authenticationToken
    }

    private fun isValidToken(jwt: String, userDetails: UserDetails) =
        jwtService.isValidToken(jwt, userDetails) && applicationTokenService.isValidToken(jwt, userDetails)

    private fun HttpServletRequest.findJwtIfPresent(): String? =
        getHeader(AUTHORIZATION)?.takeIf {
            it.startsWith(BEARER_PREFIX) && it.length > BEARER_PREFIX.length
        }?.substring(BEARER_PREFIX.length)
}
