package com.joaovictorcostadev.pequi_short.dto.group

data class GroupRequestDto(
    val name: String,
)

data class GroupResponseDto(
    val id: Long,
    val name: String,
)