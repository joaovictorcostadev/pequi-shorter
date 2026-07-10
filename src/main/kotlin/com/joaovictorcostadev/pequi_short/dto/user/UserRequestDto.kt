package com.joaovictorcostadev.pequi_short.dto.user

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull

data class UserRequestDto(
    @NotBlank
    val name:String,
    @Email
    val email: String,
    @NotBlank
    val password: String,
    @NotNull
    val groupId: Long,

)

data class UserResponseDto(
    val name: String,
    val email: String,
    val groupId: Long,
)

data class UserAuthDto(
    val email: String,
    val password: String,
)