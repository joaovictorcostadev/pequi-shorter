package com.joaovictorcostadev.pequi_short.service

import com.joaovictorcostadev.pequi_short.dto.auth.AuthResultDto
import com.joaovictorcostadev.pequi_short.dto.auth.RefreshResultDto
import com.joaovictorcostadev.pequi_short.dto.user.UserAuthRequestDto
import com.joaovictorcostadev.pequi_short.dto.user.UserLogoutRequest
import com.joaovictorcostadev.pequi_short.dto.user.UserRefreshRequestDto
import com.joaovictorcostadev.pequi_short.exception.BadRequestException
import com.joaovictorcostadev.pequi_short.exception.ForbiddenException
import com.joaovictorcostadev.pequi_short.exception.InternalServerException
import com.joaovictorcostadev.pequi_short.repository.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class AuthService(
        private val authenticatorManager: org.springframework.security.authentication.AuthenticationManager,
        private val userDetailsService: CustomUserDetailsService,
        private val refreshTokenService: RefreshTokenService,
        private val userRepository: UserRepository,
        private val tokenService: TokenService,

        @Value("\${jwt.expiration}")
        private val accessExpiration: Long,
        @Value("\${jwt.refresh_expiration}")
        private val refreshExpiration: Long,
    )
    {

    @Transactional
    fun authenticate(request: UserAuthRequestDto, group: Long): AuthResultDto {
        authenticatorManager.authenticate(
            org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                request.email, request.password
            )
        )

        val user = userRepository.findByEmail(request.email)

        if (user == null || user.group.id != group) {
            throw ForbiddenException("Access denied! Invalid credentials for this login portal.")
        }

        val userDetails = userDetailsService.loadUserByUsername(request.email)
        val token = tokenService.generateToken(userDetails)
        val refresh = refreshTokenService.generateToken(userDetails)

        refreshTokenService.saveRefreshToken(user, refresh)
            ?: throw InternalServerException("Server Error - Refresh Token not configured!")

        return AuthResultDto(
            accessToken = token,
            refreshToken = refresh,
            iat = System.currentTimeMillis(),
            exp = System.currentTimeMillis() + accessExpiration,
            accessExpirationMs = accessExpiration,
            refreshExpirationMs = refreshExpiration,
        )
    }

    @Transactional
    fun logout(userLogoutRequest: UserLogoutRequest) {
        val refreshToken = refreshTokenService.getRefreshTokenByToken(userLogoutRequest.refreshToken)
            ?: throw BadRequestException("User not found!")

        if (refreshToken.isExpired()) {
            throw ForbiddenException("Refresh token expired! Please you need make a new auth.")
        }

        refreshTokenService.revokeRefreshTokens(listOf(refreshToken))
    }

    @Transactional
    fun refreshToken(userRefreshRequestDto: UserRefreshRequestDto): RefreshResultDto {
        val lastRefreshToken = refreshTokenService.getRefreshTokenByToken(userRefreshRequestDto.refreshToken)
            ?: throw BadRequestException("Token not found!")

        if (lastRefreshToken.isExpired()) {
            throw ForbiddenException("Refresh token expired! Please you need make a new auth.")
        }

        val userDetails = userDetailsService.loadUserByUsername(lastRefreshToken.user.email)
        val accessToken = tokenService.generateToken(userDetails)

        val newRefreshToken = refreshTokenService.saveRefreshToken(
            lastRefreshToken.user,
            refreshTokenService.generateToken(userDetails)
        ) ?: throw InternalServerException("Token not generated!")

        refreshTokenService.revokeRefreshTokens(listOf(lastRefreshToken))

        return RefreshResultDto(
            accessToken = accessToken,
            refreshToken = newRefreshToken.refreshToken,
            accessExpirationMs = accessExpiration,
            refreshExpirationMs = refreshExpiration,
        )
    }
}