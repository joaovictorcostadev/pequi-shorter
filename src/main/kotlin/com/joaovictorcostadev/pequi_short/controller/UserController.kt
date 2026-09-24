package com.joaovictorcostadev.pequi_short.controller

import com.joaovictorcostadev.pequi_short.dto.response.ResponseDto
import com.joaovictorcostadev.pequi_short.dto.user.UserAuthRequestDto
import com.joaovictorcostadev.pequi_short.dto.user.UserAuthResponseDto
import com.joaovictorcostadev.pequi_short.dto.user.UserLogoutRequest
import com.joaovictorcostadev.pequi_short.dto.user.UserRefreshRequestDto
import com.joaovictorcostadev.pequi_short.dto.user.UserRefreshResponseDto
import com.joaovictorcostadev.pequi_short.dto.user.UserRequestDto
import com.joaovictorcostadev.pequi_short.dto.user.UserResponseDto
import com.joaovictorcostadev.pequi_short.dto.user.UserUpdateResponseDto
import com.joaovictorcostadev.pequi_short.enum.GroupEnum
import com.joaovictorcostadev.pequi_short.service.AuthService
import com.joaovictorcostadev.pequi_short.service.CustomUserDetailsService
import com.joaovictorcostadev.pequi_short.service.TokenService
import com.joaovictorcostadev.pequi_short.service.UserService
import io.jsonwebtoken.security.Keys
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import java.time.Instant
import javax.crypto.SecretKey

@RestController
class UserController(
    private val userService: UserService,
    private val authService: AuthService
) {


    @PostMapping("api/user/auth/save")
    fun save(@Valid @RequestBody userRequest: UserRequestDto) :  ResponseEntity<ResponseDto<UserResponseDto?>> {
        return userService.save(userRequest);
    }

    @PostMapping("api/user/auth/login")
    fun auth(@Valid @RequestBody userAuthRequest: UserAuthRequestDto) : ResponseEntity<ResponseDto<UserAuthResponseDto?>> {
        return  authService.authenticate(userAuthRequest, GroupEnum.USER.id)
    }

    @PostMapping("api/user/auth/logout")
    fun logout(@Valid @RequestBody userLogoutRequest: UserLogoutRequest) : ResponseEntity<ResponseDto<String?>> {
        return authService.logout(userLogoutRequest)
    }

    @PostMapping("api/user/auth/refresh")
    fun refreshToken(@Valid @RequestBody userRefreshRequestDto: UserRefreshRequestDto) : ResponseEntity<ResponseDto<UserRefreshResponseDto?>> {
        return  authService.refreshToken(userRefreshRequestDto)
    }


    @PreAuthorize("hasAuthority('USER_GET')")
    @GetMapping("api/user/{id}")
    fun getById(@PathVariable id: Long) : ResponseEntity<ResponseDto<UserResponseDto?>> {
        return userService.get(id);
    }

    @PreAuthorize(value = "hasAuthority('USER_UPDATE')")
    @PutMapping("api/user/update/{id}")
    open fun updatedById(@RequestBody body: UserUpdateResponseDto, @PathVariable id: Long) : ResponseEntity<ResponseDto<UserResponseDto?>> {
        return userService.update(body, id)
    }

    @PreAuthorize(value = "hasAuthority('USER_DELETE')")
    @DeleteMapping("api/user/delete/{id}")
    fun  deleteById(@PathVariable id: Long) : ResponseEntity<ResponseDto<UserResponseDto?>> {
        return userService.delete(id);
    }
}