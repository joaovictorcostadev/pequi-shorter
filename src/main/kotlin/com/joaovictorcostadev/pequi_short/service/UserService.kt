package com.joaovictorcostadev.pequi_short.service

import com.joaovictorcostadev.pequi_short.dto.user.UserRequestDto
import com.joaovictorcostadev.pequi_short.dto.user.UserResponseDto
import com.joaovictorcostadev.pequi_short.dto.user.UserUpdateResponseDto
import com.joaovictorcostadev.pequi_short.entity.User
import com.joaovictorcostadev.pequi_short.enum.GroupEnum
import com.joaovictorcostadev.pequi_short.exception.ForbiddenException
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
class UserService(
    private val repository: UserRepository,
    private val groupRepository: GroupRepository,
    private val passwordEncoder: PasswordEncoder,
    private val userAuthenticated: UserAuthenticated,
) {

    /**
     * Registro de usuário padrão — sempre cria com grupo USER.
     */
    @Transactional
    fun save(user: UserRequestDto): UserResponseDto {
        val group = groupRepository.findByIdOrNull(GroupEnum.USER.id)
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

    /**
     * Busca usuário por ID — somente o próprio usuário pode acessar seus dados.
     */
    fun get(id: Long): UserResponseDto {
        val user = repository.findByIdOrNull(id)
            ?: throw NotFoundException("User not found!")

        val loggedUser = repository.findByEmail(userAuthenticated.getUsernameLogged())

        if (loggedUser?.id != id) {
            throw ForbiddenException("Forbidden! You can only access your own data.")
        }

        return UserResponseDto(
            name = user.name,
            email = user.email,
            groupId = user.group.id!!,
            id = user.id!!
        )
    }

    /**
     * Atualiza dados do usuário — somente o próprio usuário pode alterar seus dados.
     */
    @Transactional
    fun update(body: UserUpdateResponseDto, id: Long): UserResponseDto {
        val user = repository.findByIdOrNull(id)
            ?: throw NotFoundException("User not found!")

        val loggedUser = repository.findByEmail(userAuthenticated.getUsernameLogged())

        if (loggedUser?.id != id) {
            throw ForbiddenException("Forbidden! You can only update your own data.")
        }

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

    /**
     * Deleta usuário — somente o próprio usuário pode se deletar.
     */
    @Transactional
    fun delete(id: Long): UserResponseDto {
        val loggedUser = repository.findByEmail(userAuthenticated.getUsernameLogged())
        val user = repository.findByIdOrNull(id)
            ?: throw NotFoundException("User not found!")

        if (loggedUser?.id != id) {
            throw ForbiddenException("Forbidden! You can only delete your own account.")
        }

        repository.delete(user)

        return UserResponseDto(
            name = user.name,
            email = user.email,
            groupId = user.group.id!!,
            id = user.id!!
        )
    }
}