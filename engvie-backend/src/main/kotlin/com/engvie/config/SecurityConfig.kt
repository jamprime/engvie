package com.engvie.config

import com.engvie.util.JwtUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.filter.OncePerRequestFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(private val jwtUtil: JwtUtil) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers("/api/v1/auth/**").permitAll()
                it.requestMatchers("/ws/**").permitAll()
                it.requestMatchers("/actuator/**").permitAll()
                it.anyRequest().authenticated()
            }
            .exceptionHandling {
                // Return 401 (not Spring's default 403) when JWT is missing/invalid
                it.authenticationEntryPoint { _, response, _ ->
                    response.status = HttpStatus.UNAUTHORIZED.value()
                    response.contentType = "application/json"
                    response.writer.write("{\"error\":\"Unauthorized\"}")
                }
            }
            .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }

    @Bean
    fun jwtAuthFilter() = object : OncePerRequestFilter() {
        override fun doFilterInternal(
            request: HttpServletRequest,
            response: HttpServletResponse,
            filterChain: FilterChain
        ) {
            val header = request.getHeader("Authorization")
            if (header != null && header.startsWith("Bearer ")) {
                val token = header.substring(7)
                try {
                    if (jwtUtil.isTokenValid(token)) {
                        val userId = jwtUtil.getUserIdFromToken(token)
                        val auth = UsernamePasswordAuthenticationToken(
                            userId, null,
                            listOf(SimpleGrantedAuthority("ROLE_USER"))
                        )
                        SecurityContextHolder.getContext().authentication = auth
                    }
                } catch (e: Exception) {
                    // Invalid token, continue without authentication
                }
            }
            filterChain.doFilter(request, response)
        }
    }
}
