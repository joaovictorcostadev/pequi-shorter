package com.joaovictorcostadev.pequi_short.dto.group
import jakarta.validation.constraints.NotBlank;



data class GroupRequestDto(
    @NotBlank(message = "Nome é obrigatório!")
    val name: String,
)

data class GroupResponseDto(
    val id: Long,
    val name: String,
)