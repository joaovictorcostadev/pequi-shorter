package com.joaovictorcostadev.pequi_short.service

import com.joaovictorcostadev.pequi_short.dto.user.UserRefreshRequestDto
import com.joaovictorcostadev.pequi_short.entity.RefreshToken
import com.joaovictorcostadev.pequi_short.entity.User
import com.joaovictorcostadev.pequi_short.repository.RefreshTokenRepository
import com.joaovictorcostadev.pequi_short.util.hash
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date
import javax.crypto.SecretKey

@Service
class RefreshTokenService (
    @Value($$"${jwt.refresh_secret}")
    private val refreshTokeSecret: String,

    @Value($$"${jwt.refresh_expiration}")
    private val expiration: Long,

    val repository: RefreshTokenRepository
) {

    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(refreshTokeSecret.toByteArray())
    }

    fun generateToken(userDetails: UserDetails): String {
        val strHash: String = "${userDetails.username}${Instant.now().epochSecond}${key.toString()}".hash()
        return strHash;
    }

    fun getRefreshTokenByUserId(user: User) : RefreshToken? {
        val refreshToken: RefreshToken? = repository.findFirstByUserId_IdOrderByIdDesc(user.id!!)
        return refreshToken
    }

    fun getRefreshTokenByToken(token: String) : RefreshToken? {
        val refreshToken: RefreshToken =  repository.findByToken(token.hash()) ?: return null
        return refreshToken;
    }

    fun saveRefreshToken(user: User, refreshToken: String) : UserRefreshRequestDto? {

        val token: RefreshToken = RefreshToken(
            token = refreshToken.hash(),
            user = user,
            revokeAt = Instant.now().plus(15, ChronoUnit.DAYS)
        )

        val savedRefreshToken: RefreshToken = repository.save(token)

        return UserRefreshRequestDto(refreshToken = savedRefreshToken.token)
    }

    @Transactional
    fun revokeRefreshTokens(refreshTokens: List<RefreshToken>) {
            if(refreshTokens.isEmpty()) return

            val revokedAt = Instant.now()

            refreshTokens.forEach {
                it.revokeAt = revokedAt
            }

            repository.saveAll(refreshTokens)
    }
}