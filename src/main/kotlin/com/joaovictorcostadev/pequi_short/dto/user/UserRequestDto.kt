package com.joaovictorcostadev.pequi_short.dto.user

data class UserRequestDto(
    val name:String,
    val email: String,
    val password: String,
    val groupId: Int,

)

data class UserResponseDto(
    val name: String,
    val email: String,
    val groupId: Int
)

data class UserAuthDto(
    val email: String,
    val password: String,
)