package com.example.news.security

import com.example.news.domain.UserRole
import java.io.IOException
import javax.servlet.ServletException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
class SecurityConfiguration {
    companion object {
        const val ARTICLES = "/api/v1/articles/**"
        const val AUTHORS = "/api/v1/authors/**"
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    @kotlin.jvm.Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity, jwtFilter: JwtFilter): SecurityFilterChain {
        http.httpBasic().disable()
        http.formLogin().disable()
        http.csrf().disable()
        http.logout().disable()

        http.authorizeHttpRequests().antMatchers(
            "/security/users/**", // considering as an external service
            "/swagger-resources",
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
        ).permitAll()

        http.authorizeHttpRequests().antMatchers(HttpMethod.GET, ARTICLES).permitAll()

        http.authorizeHttpRequests().antMatchers(AUTHORS).hasAnyAuthority(UserRole.EDITOR.name)
        http.authorizeHttpRequests().antMatchers(HttpMethod.POST, ARTICLES).hasAnyAuthority(UserRole.EDITOR.name)
        http.authorizeHttpRequests().antMatchers(HttpMethod.PUT, ARTICLES).hasAnyAuthority(UserRole.EDITOR.name)
        http.authorizeHttpRequests().antMatchers(HttpMethod.DELETE, ARTICLES).hasAnyAuthority(UserRole.EDITOR.name)

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build();
    }
}
