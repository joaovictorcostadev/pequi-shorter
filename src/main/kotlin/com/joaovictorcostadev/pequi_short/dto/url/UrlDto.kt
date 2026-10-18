package com.joaovictorcostadev.pequi_short.dto.url

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class UrlDtoRequest(
    var name: String?,

    @NotBlank(message = "external_url is required!")
    @JsonProperty("external_url")
    var externalUrl: String,

    @JsonProperty("user_id")
    var userId: Long? = null,
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
