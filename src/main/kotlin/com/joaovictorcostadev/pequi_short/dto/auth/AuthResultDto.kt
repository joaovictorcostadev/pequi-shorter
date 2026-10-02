package com.joaovictorcostadev.pequi_short.dto.auth

import com.fasterxml.jackson.annotation.JsonProperty

data class AuthResultDto(
    @JsonProperty("access_token")
    val accessToken: String,

    @JsonProperty("refresh_token")
    val refreshToken: String,

    val iat: Long,
    val exp: Long,

    @JsonProperty("access_expiration_ms")
    val accessExpirationMs: Long,

    @JsonProperty("refresh_expiration_ms")
    val refreshExpirationMs: Long,
)

data class RefreshResultDto(
    @JsonProperty("access_token")
    val accessToken: String,

    @JsonProperty("refresh_token")
    val refreshToken: String,

    @JsonProperty("access_expiration_ms")
    val accessExpirationMs: Long,

    @JsonProperty("refresh_expiration_ms")
    val refreshExpirationMs: Long,
)
