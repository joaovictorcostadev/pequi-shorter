package com.joaovictorcostadev.pequi_short.service

import com.joaovictorcostadev.pequi_short.dto.response.ResponseDto
import com.joaovictorcostadev.pequi_short.dto.user.UserAuthRequestDto
import com.joaovictorcostadev.pequi_short.dto.user.UserAuthResponseDto
import com.joaovictorcostadev.pequi_short.dto.user.UserLogoutRequest
import com.joaovictorcostadev.pequi_short.dto.user.UserRefreshRequestDto
import com.joaovictorcostadev.pequi_short.dto.user.UserRefreshResponseDto
import com.joaovictorcostadev.pequi_short.dto.user.UserRequestDto
import com.joaovictorcostadev.pequi_short.dto.user.UserResponseDto
import com.joaovictorcostadev.pequi_short.dto.user.UserUpdateResponseDto
import com.joaovictorcostadev.pequi_short.entity.RefreshToken
import com.joaovictorcostadev.pequi_short.repository.UserRepository
import org.springframework.stereotype.Service
import com.joaovictorcostadev.pequi_short.entity.User
import com.joaovictorcostadev.pequi_short.enum.GroupEnum
import com.joaovictorcostadev.pequi_short.repository.GroupRepository
import com.joaovictorcostadev.pequi_short.security.UserAuthenticated
import com.joaovictorcostadev.pequi_short.util.hash
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.Transactional
import java.time.Instant


@Service
class UserService(
    private val repository: UserRepository,
    private val groupRepository: GroupRepository,
    private val passwordEncoder: PasswordEncoder,
    private val userAuthenticated: UserAuthenticated,
    private val userDetailsService: CustomUserDetailsService,
    private val tokenService: TokenService,
    private val authenticatorManager: AuthenticationManager,
    private val refreshTokenService: RefreshTokenService,

    @Value($$"${jwt.expiration}")
    private val accessExpiration: Long,

    @Value($$"${jwt.refresh_expiration}")
    private val refreshExpiration: Long,


) {

    @Transactional
    fun save(user: UserRequestDto) : ResponseEntity<ResponseDto<UserResponseDto>> {
        val group = groupRepository.findById(user.groupId).orElseThrow{
            RuntimeException("Group not found!")
        }

        val hashedPassword: String = passwordEncoder.encode(user.password).toString()
        val entity = User(name = user.name, email = user.email, password = hashedPassword, updatedAt = Instant.now() , group = group)
        val savedUser: User = repository.save(entity)
        return ResponseEntity.ok(
            ResponseDto(
                code = HttpStatus.OK.value(),
                message = "User created",
                data = UserResponseDto(name = savedUser.name, email = savedUser.email, groupId = savedUser.group.id!!, id = savedUser.id!!)
            )
        )
    }

    fun get(id: Long) : ResponseEntity<ResponseDto<UserResponseDto?>> {
        val user:User? = repository.findByIdOrNull(id)
        val loggedUser = repository.findByEmail(userAuthenticated.getUsernameLogged())

        if(user == null) {
            return ResponseEntity
                .badRequest().
                body(
                    ResponseDto(
                        code = HttpStatus.BAD_REQUEST.value(),
                        data = null,
                        message = "User not found!"
                    )
                )
        }

        if(loggedUser?.id != id && loggedUser?.group?.id != GroupEnum.ADMIN.id) {
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(
                    ResponseDto(
                        code = HttpStatus.FORBIDDEN.value(),
                        data = null,
                        message = "Forbidden!"
                    )
                )
        }

        return ResponseEntity
            .ok()
            .body(
                ResponseDto(
                    code = HttpStatus.OK.value(),
                    data = UserResponseDto(user.name, email = user.email, groupId = user.group.id!!, id = user.id!!),
                    message = "User found!"
                )
            )
    }

    @Transactional
    fun update(body: UserUpdateResponseDto, id:Long) : ResponseEntity<ResponseDto<UserResponseDto?>> {
        val user:User? = repository.findByIdOrNull(id)
        val loggedUser = repository.findByEmail(userAuthenticated.getUsernameLogged())

        if(user == null) {
            return ResponseEntity
                .badRequest().
                body(
                    ResponseDto(
                        code = HttpStatus.BAD_REQUEST.value(),
                        data = null,
                        message = "User not found!"
                    )
                )
        }

        if(loggedUser?.id != id && loggedUser?.group?.id != GroupEnum.ADMIN.id) {
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(
                    ResponseDto(
                        code = HttpStatus.FORBIDDEN.value(),
                        data = null,
                        message = "Forbidden!"
                    )
                )
        }

        user.email = body.email ?: user.email
        user.name = body.name ?:  user.name
        repository.save(user)

        return ResponseEntity
            .ok()
            .body(
                ResponseDto(
                    code = HttpStatus.OK.value(),
                    data = UserResponseDto(user.name, email = user.email, groupId = user.group.id!!, id = user.id!!),
                    message = "User updated!"
                )
            )

    }

    @Transactional
    fun delete(id: Long) : ResponseEntity<ResponseDto<UserResponseDto?>> {
        val user: User = repository.findByIdOrNull(id) ?: return ResponseEntity
            .badRequest().body(
                ResponseDto(
                    code = HttpStatus.BAD_REQUEST.value(),
                    data = null,
                    message = "User not found!"
                )
            )

        repository.delete(user)

        return ResponseEntity
            .ok()
            .body(
                ResponseDto(
                    code = HttpStatus.OK.value(),
                    data = UserResponseDto(name = user.name, email = user.email, groupId = user.group.id!!, id = user.id!!),
                    message = "User Deleted!"
                    )
            )

    }

    @Transactional
    fun auth(userAuthRequest: UserAuthRequestDto) : ResponseEntity<ResponseDto<UserAuthResponseDto?>> {
        authenticatorManager.authenticate(
            UsernamePasswordAuthenticationToken(userAuthRequest.email, userAuthRequest.password)
        )

        val userDetails = userDetailsService.loadUserByUsername(userAuthRequest.email)
        val token = tokenService.generateToken(userDetails)
        val refresh = refreshTokenService.generateToken(userDetails)
        val user: User? = repository.findByEmail(userAuthRequest.email)
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
            .maxAge(accessExpiration / 1000)
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

        val cookie: ResponseCookie = ResponseCookie.from(
            "access_token", "")
            .secure(true)
            .httpOnly(true)
            .path("/")
            .maxAge(0)
            .build()

        val refreshTokenCookie: ResponseCookie = ResponseCookie.from(
            "refresh_token", "")
            .secure(true)
            .httpOnly(true)
            .path("/")
            .maxAge(0)
            .build()

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

        val cookie: ResponseCookie = ResponseCookie.from(
            "access_token", accessToken)
            .secure(true)
            .httpOnly(true)
            .path("/")
            .maxAge(accessExpiration / 1000)
            .build()

        val refreshTokenCookie: ResponseCookie = ResponseCookie.from(
            "refresh_token", newRefreshToken.refreshToken)
            .secure(true)
            .httpOnly(true)
            .path("/")
            .maxAge(accessExpiration / 1000)
            .build()

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

    fun handlingRefreshToken(
        user: User?,
        refreshToken: String
    ): String? {

        user ?: return null

        refreshTokenService.saveRefreshToken(user, refreshToken)

        return refreshToken
    }
}