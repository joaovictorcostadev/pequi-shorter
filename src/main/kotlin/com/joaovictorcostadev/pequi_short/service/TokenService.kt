package com.joaovictorcostadev.pequi_short.service
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.Date
import javax.crypto.SecretKey

@Service
class TokenService (
    @Value($$"${jwt.secret}")
    private val secretString: String,

    @Value($$"${jwt.expiration}")
    private val expiration: Long

    ) {

    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(secretString.toByteArray())
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

    fun extractUsername(token: String) : String {
        return getClaims(token).subject
    }

    fun isTokenValid(token: String, userDetails: UserDetails) : Boolean {
        val username: String = extractUsername(token)
        val expiration = getClaims(token).expiration

        return username == userDetails.username && expiration.after(Date())
    }


    private fun getClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
    }
}