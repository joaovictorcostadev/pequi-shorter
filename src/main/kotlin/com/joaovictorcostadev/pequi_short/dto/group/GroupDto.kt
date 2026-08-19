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

data class GroupUpdateRequestDto(
    @NotBlank(message = "id is required!")
    val id: Long,

    @NotBlank(message = "name is required!")
    val name: String,
)