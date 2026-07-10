package com.joaovictorcostadev.pequi_short.controller

import com.joaovictorcostadev.pequi_short.dto.user.UserRequestDto
import com.joaovictorcostadev.pequi_short.dto.user.UserResponseDto
import com.joaovictorcostadev.pequi_short.service.UserService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import jakarta.validation.Valid

@RestController
class UserController(private val userService: UserService) {

    @PostMapping("api/user/save")
    fun save(@Valid @RequestBody userRequest: UserRequestDto) : UserResponseDto{
        return userService.create(userRequest);
    }
}