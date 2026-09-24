package com.joaovictorcostadev.pequi_short.service

import com.joaovictorcostadev.pequi_short.dto.response.ResponseDto
import com.joaovictorcostadev.pequi_short.dto.user.UserAuthRequestDto
import com.joaovictorcostadev.pequi_short.dto.user.UserAuthResponseDto
import com.joaovictorcostadev.pequi_short.dto.user.UserLogoutRequest
import com.joaovictorcostadev.pequi_short.dto.user.UserRefreshRequestDto
import com.joaovictorcostadev.pequi_short.dto.user.UserRefreshResponseDto
import com.joaovictorcostadev.pequi_short.entity.RefreshToken
import com.joaovictorcostadev.pequi_short.entity.User
import com.joaovictorcostadev.pequi_short.repository.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class AuthService(
        private val authenticatorManager: AuthenticationManager,
        private val userDetailsService: CustomUserDetailsService,
        private val refreshTokenService: RefreshTokenService,
        private val userRepository: UserRepository,

        private val tokenService: TokenService,



        @Value($$"${jwt.expiration}")
        private val accessExpiration: Long,
        @Value($$"${jwt.refresh_expiration}")
        private val refreshExpiration: Long,
    )
    {

    fun authenticate(request: UserAuthRequestDto, group: Long) : ResponseEntity<ResponseDto<UserAuthResponseDto?>> {
        return authenticateUser(request, group)
    }

    @Transactional
    private fun authenticateUser(userAuthRequest: UserAuthRequestDto, group: Long) : ResponseEntity<ResponseDto<UserAuthResponseDto?>> {
        authenticatorManager.authenticate(
            UsernamePasswordAuthenticationToken(userAuthRequest.email, userAuthRequest.password)
        )

        val userDetails = userDetailsService.loadUserByUsername(userAuthRequest.email)
        val token = tokenService.generateToken(userDetails)
        val refresh = refreshTokenService.generateToken(userDetails)
        val user: User? = userRepository.findByEmail(userAuthRequest.email)
        val cookie: ResponseCookie = ResponseCookie.from(
            "access_token", token)
            .secure(true)
            .httpOnly(true)
            .path("/")
            .maxAge(accessExpiration / 1000)
            .build()

        val refreshTokenCookie: ResponseCookie = ResponseCookie.from(
            "refresh_token", refresh)
            .secure(true)
            .httpOnly(true)
            .path("/")
            .maxAge(refreshExpiration / 1000)
            .build()

        val savedRefreshToken = handlingRefreshToken(user, refresh) ?: return ResponseEntity.internalServerError().body(
            ResponseDto(
                code = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                data = null,
                message = "Server Error - Refresh Token not configured!"
            )
        )

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
            .body(
                ResponseDto(
                    code = HttpStatus.OK.value(),
                    message = "Authorized",
                    data = UserAuthResponseDto(
                        token = token,
                        refresh = savedRefreshToken,
                        iat = System.currentTimeMillis(),
                        exp = System.currentTimeMillis() + accessExpiration)

                )
            )

    }

        @Transactional
        private fun auth(userAuthRequest: UserAuthRequestDto) : ResponseEntity<ResponseDto<UserAuthResponseDto?>> {
            authenticatorManager.authenticate(
                UsernamePasswordAuthenticationToken(userAuthRequest.email, userAuthRequest.password)
            )

            val userDetails = userDetailsService.loadUserByUsername(userAuthRequest.email)
            val token = tokenService.generateToken(userDetails)
            val refresh = refreshTokenService.generateToken(userDetails)
            val user: User? = userRepository.findByEmail(userAuthRequest.email)
            val cookie: ResponseCookie = ResponseCookie.from(
                "access_token", token)
                .secure(true)
                .httpOnly(true)
                .path("/")
                .maxAge(accessExpiration / 1000)
                .build()

            val refreshTokenCookie: ResponseCookie = ResponseCookie.from(
                "refresh_token", refresh)
                .secure(true)
                .httpOnly(true)
                .path("/")
                .maxAge(refreshExpiration / 1000)
                .build()

            val savedRefreshToken = handlingRefreshToken(user, refresh) ?: return ResponseEntity.internalServerError().body(
                ResponseDto(
                    code = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    data = null,
                    message = "Server Error - Refresh Token not configured!"
                )
            )

            return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(
                    ResponseDto(
                        code = HttpStatus.OK.value(),
                        message = "Authorized",
                        data = UserAuthResponseDto(
                            token = token,
                            refresh = savedRefreshToken,
                            iat = System.currentTimeMillis(),
                            exp = System.currentTimeMillis() + accessExpiration)

                    )
                )

        }

        @Transactional
        fun logout(userLogoutRequest: UserLogoutRequest) : ResponseEntity<ResponseDto<String?>> {

            val refreshToken: RefreshToken = refreshTokenService.getRefreshTokenByToken(userLogoutRequest.refreshToken) ?:
            return ResponseEntity
                .badRequest().
                body(
                    ResponseDto(
                        code = HttpStatus.BAD_REQUEST.value(),
                        data = null,
                        message = "User not found!"
                    )
                )

            if(refreshToken.isExpired()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    ResponseDto(
                        code = HttpStatus.FORBIDDEN.value(),
                        data = null,
                        message = "Refresh token expired! Please you need make a new auth.")
                )
            }

            val cookie: ResponseCookie = createSecureCookie("access_token", "", 0)
            val refreshTokenCookie: ResponseCookie = createSecureCookie("refresh_token", "", 0)

            refreshTokenService.revokeRefreshTokens(listOf(refreshToken))

            return ResponseEntity
                .noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .build()
        }

        @Transactional
        fun refreshToken(userRefreshRequestDto: UserRefreshRequestDto) : ResponseEntity<ResponseDto<UserRefreshResponseDto?>> {
            val lastRefreshToken: RefreshToken = refreshTokenService.getRefreshTokenByToken(userRefreshRequestDto.refreshToken) ?:
            return ResponseEntity.badRequest().body(
                ResponseDto(
                    code = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    data = null,
                    message = "Token not found!")
            )

            if(lastRefreshToken.isExpired()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    ResponseDto(
                        code = HttpStatus.FORBIDDEN.value(),
                        data = null,
                        message = "Refresh token expired! Please you need make a new auth.")
                )
            }

            val userDetails: UserDetails = userDetailsService.loadUserByUsername(lastRefreshToken.user.email)
            val accessToken = tokenService.generateToken(userDetails)

            // Create a new refresh token
            val newRefreshToken: UserRefreshRequestDto = refreshTokenService.saveRefreshToken(lastRefreshToken.user, refreshTokenService.generateToken(userDetails)) ?:
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDto(code = HttpStatus.INTERNAL_SERVER_ERROR.value(), data = null, message = "Token not generated!"))

            val cookie: ResponseCookie = createSecureCookie("access_token", accessToken, accessExpiration / 1000)

            val refreshTokenCookie: ResponseCookie = createSecureCookie("refresh_token", newRefreshToken.refreshToken, refreshExpiration / 1000)

            // Revoke a lastRefreshToken
            refreshTokenService.revokeRefreshTokens(listOf(lastRefreshToken))

            return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(
                    ResponseDto(
                        code = HttpStatus.OK.value(),
                        data = UserRefreshResponseDto(accessToken =  accessToken, refreshToken =  newRefreshToken.refreshToken),
                        message = "Access Token refreshed!"
                    )
                )
        }

        private fun createSecureCookie(key: String, value: String, exp: Long): ResponseCookie {

            return ResponseCookie.from(
                key, value)
                .secure(true)
                .httpOnly(true)
                .path("/")
                .maxAge(exp)
                .build()
        }

        private fun handlingRefreshToken(
            user: User?,
            refreshToken: String
        ): String? {
            user ?: return null

            refreshTokenService.saveRefreshToken(user, refreshToken)

            return refreshToken
        }
}