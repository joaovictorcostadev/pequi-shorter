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
        return Jwts.builder()
            .header()
            .type("JWT")
            .and()
            .subject(userDetails.username)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + expiration))
            .signWith(key)
            .compact()
    }


    fun getClaims(token: String) : Claims {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
    }

    fun extractUsername(token: String) : String {
        return getClaims(token).subject
    }

    fun isTokenValid(token: String, userDetails: UserDetails) : Boolean {
        val username: String = extractUsername(token)
        val expiration = getClaims(token).expiration

        return username == userDetails.username && expiration.after(Date())
    }

    fun getRefreshToken(user: User) : RefreshToken? {
        val refreshToken: RefreshToken? = repository.findFirstByUserId_IdOrderByIdDesc(user.id!!)
        return refreshToken
    }

    fun getRefreshTokenByToken(token: String) : RefreshToken? {
        val refreshToken: MutableList<RefreshToken> = repository.findByToken(token.hash())
        if(refreshToken.isEmpty()) return null
        return refreshToken.first();
    }

    fun saveRefreshToken(user: User, refreshToken: String) : UserRefreshRequestDto? {

        val token: RefreshToken = RefreshToken(
            token = refreshToken.hash(),
            user = user,
            revokeAt = Instant.now().plus(15, ChronoUnit.DAYS)
        )

        val savedRefreshToken: RefreshToken = repository.save(token)

        return UserRefreshRequestDto(token = savedRefreshToken.token)
    }
}