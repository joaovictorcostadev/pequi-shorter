package com.joaovictorcostadev.pequi_short.controller

import com.joaovictorcostadev.pequi_short.dto.response.ResponseDto
import com.joaovictorcostadev.pequi_short.dto.user.AdminUserRequestDto
import com.joaovictorcostadev.pequi_short.dto.user.UserAuthRequestDto
import com.joaovictorcostadev.pequi_short.dto.user.UserAuthResponseDto
import com.joaovictorcostadev.pequi_short.dto.user.UserLogoutRequest
import com.joaovictorcostadev.pequi_short.dto.user.UserRefreshRequestDto
import com.joaovictorcostadev.pequi_short.dto.user.UserRefreshResponseDto
import com.joaovictorcostadev.pequi_short.dto.user.UserResponseDto
import com.joaovictorcostadev.pequi_short.dto.user.UserUpdateResponseDto
import com.joaovictorcostadev.pequi_short.enum.GroupEnum
import com.joaovictorcostadev.pequi_short.service.AdminUserService
import com.joaovictorcostadev.pequi_short.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/admin/")
class AdminUserController(
    private val adminUserService: AdminUserService,
    private val authService: AuthService
) {

    // ---- Endpoints protegidos (requerem autenticação + permissão ADMIN) ----

    @PreAuthorize("hasAuthority('ADMIN_USER_CREATE')")
    @PostMapping("user/save")
    fun save(@Valid @RequestBody userRequest: AdminUserRequestDto): ResponseEntity<ResponseDto<UserResponseDto>> {
        val data = adminUserService.save(userRequest)
        return ResponseEntity.ok(ResponseDto(code = HttpStatus.OK.value(), data = data, message = "User created"))
    }

    @PreAuthorize("hasAuthority('ADMIN_USER_GET')")
    @GetMapping("user/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<ResponseDto<UserResponseDto>> {
        val data = adminUserService.get(id)
        return ResponseEntity.ok(ResponseDto(code = HttpStatus.OK.value(), data = data, message = "User found!"))
    }

    @PreAuthorize("hasAuthority('ADMIN_USER_UPDATE')")
    @PutMapping("user/update/{id}")
    fun updatedById(@Valid @RequestBody body: UserUpdateResponseDto, @PathVariable id: Long): ResponseEntity<ResponseDto<UserResponseDto>> {
        val data = adminUserService.update(body, id)
        return ResponseEntity.ok(ResponseDto(code = HttpStatus.OK.value(), data = data, message = "User updated!"))
    }

    @PreAuthorize("hasAuthority('ADMIN_USER_DELETE')")
    @DeleteMapping("user/delete/{id}")
    fun deleteById(@PathVariable id: Long): ResponseEntity<ResponseDto<UserResponseDto>> {
        val data = adminUserService.delete(id)
        return ResponseEntity.ok(ResponseDto(code = HttpStatus.OK.value(), data = data, message = "User Deleted!"))
    }

    // ---- Endpoints públicos (auth) ----

    @PostMapping("user/auth/login")
    fun auth(@Valid @RequestBody userAuthRequest: UserAuthRequestDto): ResponseEntity<ResponseDto<UserAuthResponseDto>> {
        val result = authService.authenticate(userAuthRequest, GroupEnum.ADMIN.id)

        val cookie = createSecureCookie("access_token", result.accessToken, result.accessExpirationMs / 1000)
        val refreshCookie = createSecureCookie("refresh_token", result.refreshToken, result.refreshExpirationMs / 1000)

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body(
                ResponseDto(
                    code = HttpStatus.OK.value(),
                    message = "Authorized",
                    data = UserAuthResponseDto(
                        token = result.accessToken,
                        refresh = result.refreshToken,
                        iat = result.iat,
                        exp = result.exp
                    )
                )
            )
    }

    @PostMapping("user/auth/logout")
    fun logout(@Valid @RequestBody userLogoutRequest: UserLogoutRequest): ResponseEntity<Void> {
        authService.logout(userLogoutRequest)

        val cookie = createSecureCookie("access_token", "", 0)
        val refreshCookie = createSecureCookie("refresh_token", "", 0)

        return ResponseEntity.noContent()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .build()
    }

    @PostMapping("user/auth/refresh")
    fun refreshToken(@Valid @RequestBody userRefreshRequestDto: UserRefreshRequestDto): ResponseEntity<ResponseDto<UserRefreshResponseDto>> {
        val result = authService.refreshToken(userRefreshRequestDto)

        val cookie = createSecureCookie("access_token", result.accessToken, result.accessExpirationMs / 1000)
        val refreshCookie = createSecureCookie("refresh_token", result.refreshToken, result.refreshExpirationMs / 1000)

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body(
                ResponseDto(
                    code = HttpStatus.OK.value(),
                    data = UserRefreshResponseDto(accessToken = result.accessToken, refreshToken = result.refreshToken),
                    message = "Access Token refreshed!"
                )
            )
    }

    private fun createSecureCookie(key: String, value: String, maxAge: Long): ResponseCookie {
        return ResponseCookie.from(key, value)
            .secure(true)
            .httpOnly(true)
            .path("/")
            .maxAge(maxAge)
            .build()
    }
}
