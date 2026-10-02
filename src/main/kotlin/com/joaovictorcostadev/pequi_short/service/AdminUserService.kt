package com.joaovictorcostadev.pequi_short.service

import com.joaovictorcostadev.pequi_short.dto.user.AdminUserRequestDto
import com.joaovictorcostadev.pequi_short.dto.user.UserResponseDto
import com.joaovictorcostadev.pequi_short.dto.user.UserUpdateResponseDto
import com.joaovictorcostadev.pequi_short.entity.User
import com.joaovictorcostadev.pequi_short.exception.NotFoundException
import com.joaovictorcostadev.pequi_short.repository.GroupRepository
import com.joaovictorcostadev.pequi_short.repository.UserRepository
import com.joaovictorcostadev.pequi_short.security.UserAuthenticated
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class AdminUserService(
    private val repository: UserRepository,
    private val groupRepository: GroupRepository,
    private val passwordEncoder: PasswordEncoder,
    private val userAuthenticated: UserAuthenticated,
) {

    @Transactional
    fun save(user: AdminUserRequestDto): UserResponseDto {
        val group = groupRepository.findByIdOrNull(user.groupId)
            ?: throw NotFoundException("Group not found!")

        val hashedPassword: String = passwordEncoder.encode(user.password).toString()
        val entity = User(name = user.name, email = user.email, password = hashedPassword, updatedAt = Instant.now(), group = group)
        val savedUser: User = repository.save(entity)

        return UserResponseDto(
            name = savedUser.name,
            email = savedUser.email,
            groupId = savedUser.group.id!!,
            id = savedUser.id!!
        )
    }

    fun get(id: Long): UserResponseDto {
        val user = repository.findByIdOrNull(id)
            ?: throw NotFoundException("User not found!")

        return UserResponseDto(
            name = user.name,
            email = user.email,
            groupId = user.group.id!!,
            id = user.id!!
        )
    }

    @Transactional
    fun update(body: UserUpdateResponseDto, id: Long): UserResponseDto {
        val user = repository.findByIdOrNull(id)
            ?: throw NotFoundException("User not found!")

        user.email = body.email ?: user.email
        user.name = body.name ?: user.name
        repository.save(user)

        return UserResponseDto(
            name = user.name,
            email = user.email,
            groupId = user.group.id!!,
            id = user.id!!
        )
    }

    @Transactional
    fun delete(id: Long): UserResponseDto {
        val user = repository.findByIdOrNull(id)
            ?: throw NotFoundException("User not found!")

        repository.delete(user)

        return UserResponseDto(
            name = user.name,
            email = user.email,
            groupId = user.group.id!!,
            id = user.id!!
        )
    }
}