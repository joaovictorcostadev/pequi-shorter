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
    @JsonProperty("refresh_token")
    val refresh: String,
    val exp: Long,
    val iat: Long,
)

data class UserUpdateResponseDto(
    val email: String?,
    val name: String?
)

data class UserRefreshRequestDto(
    @JsonProperty(value = "refresh_token")
    @NotBlank(message = "refresh_token is required!")
    val refreshToken: String,
)

data class UserRefreshResponseDto(
    @JsonProperty("access_token")
    val accessToken: String,

    @JsonProperty(value = "refresh_token")
    val refreshToken: String,
)

data class UserLogoutRequest(
    @NotBlank(message = "refresh_token is required!")
    @JsonProperty(value = "refresh_token")
    val refreshToken: String,
)