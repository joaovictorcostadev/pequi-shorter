package com.joaovictorcostadev.pequi_short.security

import com.joaovictorcostadev.pequi_short.service.TokenService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val tokenService: TokenService,
    private val userDetailsService: UserDetailsService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        var jwt: String? = null
        val authHeader = request.getHeader("Authorization")

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7)
        } else {
            val tokenCookie = request.cookies?.firstOrNull { it.name == "access_token" }
            if (tokenCookie != null && tokenCookie.value.isNotBlank()) {
                jwt = tokenCookie.value
            }
        }

        if (jwt.isNullOrBlank()) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            val userEmail = tokenService.extractUsername(jwt)

            if (SecurityContextHolder.getContext().authentication == null) {
                val userDetails = userDetailsService.loadUserByUsername(userEmail)

                if (tokenService.isTokenValid(jwt, userDetails)) {
                    val authToken = UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.authorities
                    )
                    authToken.details = WebAuthenticationDetailsSource().buildDetails(request)

                    // Define o usuário como autenticado no Spring Security
                    SecurityContextHolder.getContext().authentication = authToken
                }
            }
        } catch (e: Exception) {
            // Se o token for inválido/expirado, continua sem autenticar
        }

        filterChain.doFilter(request, response)
    }
}