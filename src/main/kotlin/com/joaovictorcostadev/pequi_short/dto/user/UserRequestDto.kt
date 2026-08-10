package com.joaovictorcostadev.pequi_short.dto.user

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull
import java.time.Instant

data class UserRequestDto(
    @NotBlank
    val name:String,
    @Email
    val email: String,
    @NotBlank
    val password: String,
    @NotNull
    @JsonProperty("group_id")
    val groupId: Long,

)

data class UserResponseDto(
    val name: String,
    val email: String,
    @JsonProperty("group_id")
    val groupId: Long,
    val id: Long
)

data class UserAuthRequestDto(
    val email: String,
    val password: String,
)

data class UserAuthResponseDto(
    val token: String,
    val exp: Long,
    val iat: Long,
)

data class UserUpdateResponseDto(
    val email: String?,
    val name: String?
)