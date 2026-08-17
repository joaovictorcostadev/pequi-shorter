package com.joaovictorcostadev.pequi_short.dto.url

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class UrlDtoRequest(
    @NotBlank(message = "name is required!")
    val name: String,

    @NotBlank(message = "external_url is required!")
    @JsonProperty("external_url")
    val externalUrl: String,

    @NotNull(message = "user_id is required!")
    @JsonProperty("user_id")
    val userId: Long,

)

data class UrlDtoResponse(
    val id: Long,
    val name: String,
    @JsonProperty("external_url")
    val externalUrl: String,
    @JsonProperty("user_id")
    val userId: Long,
    val url: String,
)
