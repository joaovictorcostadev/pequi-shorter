package com.joaovictorcostadev.pequi_short.service

import com.joaovictorcostadev.pequi_short.dto.user.UserRequestDto
import com.joaovictorcostadev.pequi_short.dto.user.UserResponseDto
import com.joaovictorcostadev.pequi_short.repository.UserRepository
import org.springframework.stereotype.Service
import com.joaovictorcostadev.pequi_short.entity.User
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.Instant
import kotlin.time.Clock


@Service
class UserService(
    private val repository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun create(user: UserRequestDto) : UserResponseDto {
        val hashedPassword: String = passwordEncoder.encode(user.password).toString();
        val entity: User = User(name = user.name, email = user.email, password = hashedPassword, updatedAt = Instant.now() , groupId = user.groupId)
        val savedUser: User = repository.save(entity)
        return UserResponseDto(name = savedUser.name, email = savedUser.email, groupId = savedUser.groupId)
    }
}