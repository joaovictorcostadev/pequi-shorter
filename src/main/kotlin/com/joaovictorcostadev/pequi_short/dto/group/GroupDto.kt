package com.joaovictorcostadev.pequi_short.dto.group
import jakarta.validation.constraints.NotBlank;

data class GroupRequestDto(
    @NotBlank(message = "name is required!")
    val name: String,
)

data class GroupResponseDto(
    val id: Long,
    val name: String,
)