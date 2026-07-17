package com.joaovictorcostadev.pequi_short.controller

import com.joaovictorcostadev.pequi_short.dto.response.ResponseDto
import com.joaovictorcostadev.pequi_short.dto.user.UserAuthRequestDto
import com.joaovictorcostadev.pequi_short.dto.user.UserAuthResponseDto
import com.joaovictorcostadev.pequi_short.dto.user.UserRequestDto
import com.joaovictorcostadev.pequi_short.dto.user.UserResponseDto
import com.joaovictorcostadev.pequi_short.service.CustomUserDetailsService
import com.joaovictorcostadev.pequi_short.service.TokenService
import com.joaovictorcostadev.pequi_short.service.UserService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

@RestController
class UserController(
    private val userService: UserService,
    private val userDetailsService: CustomUserDetailsService,
    private val tokenService: TokenService,
    private val authenticatorManager: AuthenticationManager
    ) {

    @PostMapping("api/user/auth/save")
    fun save(@Valid @RequestBody userRequest: UserRequestDto) :  ResponseEntity<ResponseDto<UserResponseDto>> {
        return userService.save(userRequest);
    }

    @PostMapping("api/user/auth/login")
    fun auth(@Valid @RequestBody userAuthRequest: UserAuthRequestDto) : ResponseEntity<ResponseDto<UserAuthResponseDto>> {

        authenticatorManager.authenticate(
            UsernamePasswordAuthenticationToken(userAuthRequest.email, userAuthRequest.password)
        )

        val userDetails = userDetailsService.loadUserByUsername(userAuthRequest.email)
        val token = tokenService.generateToken(userDetails)

        return ResponseEntity.ok(ResponseDto(code = HttpStatus.OK.value(), message = "Authorized", data = UserAuthResponseDto(token)))
    }
}