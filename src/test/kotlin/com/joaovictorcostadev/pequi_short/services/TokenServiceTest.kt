package com.joaovictorcostadev.pequi_short.services

import com.joaovictorcostadev.pequi_short.service.TokenService
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TokenServiceTest {

    private val secret = "948bd69c93866c12a1b647027404dd12cf7ccde1a76cdc1b87e28638c7d175a3"
    private val tokenService = TokenService(secretString = secret, expiration = 900000)
    
    private val userDetails: UserDetails = User
        .withUsername("testUser")
        .password("password")
        .roles("USER")
        .build()

    // ----------------------------------------------------------------------
    // TOKEN GENERATION TESTS
    // ----------------------------------------------------------------------

    @Test
    fun `should generate token`() {
        val token = tokenService.generateToken(userDetails)
        
        assertNotNull(token, "Token should not be null")
        assertTrue(token.isNotEmpty(), "Token should not be empty")
    }

    // ----------------------------------------------------------------------
    // USERNAME EXTRACTION TESTS
    // ----------------------------------------------------------------------

    @Test
    fun `should extract username from valid token`() {
        val token = tokenService.generateToken(userDetails)
        val extractedUsername = tokenService.extractUsername(token)

        assertEquals("testUser", extractedUsername, "Extracted username should match userDetails username")
    }

    @Test
    fun `should throw exception when extracting username from expired token`() {
        // Negative expiration makes the token expire instantly
        val expiredTokenService = TokenService(secretString = secret, expiration = -1000)
        val token = expiredTokenService.generateToken(userDetails)

        // JJWT will throw an exception, so we use assertFailsWith
        kotlin.test.assertFailsWith<io.jsonwebtoken.ExpiredJwtException> {
            expiredTokenService.extractUsername(token)
        }
    }

    @Test
    fun `should throw exception when extracting username from tampered token`() {
        val token = tokenService.generateToken(userDetails)
        val tamperedToken = token + "xyz"

        // JJWT detects that the signature does not match
        kotlin.test.assertFailsWith<io.jsonwebtoken.JwtException> {
            tokenService.extractUsername(tamperedToken)
        }
    }

    // ----------------------------------------------------------------------
    // TOKEN VALIDATION TESTS (isTokenValid)
    // ----------------------------------------------------------------------

    @Test
    fun `should return true when token is valid and belongs to correct user`() {
        val token = tokenService.generateToken(userDetails)
        val isValid = tokenService.isTokenValid(token, userDetails)

        assertTrue(isValid, "Token should be successfully validated")
    }

    @Test
    fun `should return false when token belongs to another user`() {
        val token = tokenService.generateToken(userDetails)
        
        val otherUser = User.withUsername("otherUser").password("pass").roles("USER").build()
        val isValid = tokenService.isTokenValid(token, otherUser)

        assertFalse(isValid, "Should return false because the token was generated for testUser, not otherUser")
    }

    @Test
    fun `should return false when token is expired`() {
        // Since we updated TokenService.kt, it catches the exception and returns false
        val expiredTokenService = TokenService(secretString = secret, expiration = -1000)
        val token = expiredTokenService.generateToken(userDetails)

        val isValid = expiredTokenService.isTokenValid(token, userDetails)

        assertFalse(isValid, "Should return false because the token is already expired")
    }

    @Test
    fun `should return false when token is tampered or malformed`() {
        val token = tokenService.generateToken(userDetails)
        val tamperedToken = token + "tampered"

        val isValid = tokenService.isTokenValid(tamperedToken, userDetails)

        assertFalse(isValid, "Should return false because the token was tampered with")
    }
}   