package com.joaovictorcostadev.pequi_short.dto.url

import com.joaovictorcostadev.pequi_short.entity.UrlAccess

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import java.time.Instant

data class UrlAccessDTO(
    @NotBlank(message = "user_id is required!")
    @JsonProperty("user_id")
    val userId: Long,

    @NotBlank(message = "ip is required!")
    val ip: String,

    @NotBlank(message = "user_id is required!")
    @JsonProperty("url_id")
    val urlId: Long,

    @NotBlank(message = "country is required!")
    val country: String,

    @NotBlank(message = "state is required")
    val state: String,

    @NotBlank(message = "city is required")
    val city: String,

    @NotBlank(message = "device_type is required")
    val deviceType: String? = null,

    @JsonProperty("operating_system")
    val operatingSystem: String? = null,

    val browser: String? = null,

    @JsonProperty("user_agent")
    val userAgent: String? = null,

    val referrer: String? = null,

    val updatedAt: Instant
)