package com.joaovictorcostadev.pequi_short.dto.rule

import jakarta.validation.constraints.NotBlank


data class RuleRequestDto(
    @NotBlank(message = "name is required!")
    val name: String
)

data class RuleResponseDto(
    val id: Long,
    val name: String,
)
