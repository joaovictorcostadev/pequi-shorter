package com.joaovictorcostadev.pequi_short.service

import com.joaovictorcostadev.pequi_short.dto.response.ResponseDto
import com.joaovictorcostadev.pequi_short.dto.user.UserRequestDto
import com.joaovictorcostadev.pequi_short.dto.user.UserResponseDto
import com.joaovictorcostadev.pequi_short.dto.user.UserUpdateResponseDto
import com.joaovictorcostadev.pequi_short.entity.Group
import com.joaovictorcostadev.pequi_short.entity.User
import com.joaovictorcostadev.pequi_short.enum.GroupEnum
import com.joaovictorcostadev.pequi_short.repository.GroupRepository
import com.joaovictorcostadev.pequi_short.repository.UserRepository
import com.joaovictorcostadev.pequi_short.security.UserAuthenticated
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
    fun save(user: UserRequestDto): ResponseEntity<ResponseDto<UserResponseDto?>> {

        val group: Group = groupRepository.findByIdOrNull(GroupEnum.USER.id)
            ?: return ResponseEntity(ResponseDto(code = HttpStatus.NOT_FOUND.value(), data = null, message = "Group not found!"), HttpStatus.NOT_FOUND)

        val hashedPassword: String = passwordEncoder.encode(user.password).toString()
        val entity = User(name = user.name, email = user.email, password = hashedPassword, updatedAt = Instant.now(), group = group)
        val savedUser: User = repository.save(entity)

        return ResponseEntity.ok(
            ResponseDto(
                code = HttpStatus.OK.value(),
                message = "User created",
                data = UserResponseDto(name = savedUser.name, email = savedUser.email, groupId = savedUser.group.id!!, id = savedUser.id!!)
            )
        )
    }

    /**
     * Busca usuário por ID — somente o próprio usuário pode acessar seus dados.
     */
    fun get(id: Long): ResponseEntity<ResponseDto<UserResponseDto?>> {
        val user: User? = repository.findByIdOrNull(id)
        val loggedUser = repository.findByEmail(userAuthenticated.getUsernameLogged())

        if (user == null) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                    ResponseDto(
                        code = HttpStatus.NOT_FOUND.value(),
                        data = null,
                        message = "User not found!"
                    )
                )
        }

        if (loggedUser?.id != id) {
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(
                    ResponseDto(
                        code = HttpStatus.FORBIDDEN.value(),
                        data = null,
                        message = "Forbidden! You can only access your own data."
                    )
                )
        }

        return ResponseEntity
            .ok()
            .body(
                ResponseDto(
                    code = HttpStatus.OK.value(),
                    data = UserResponseDto(user.name, email = user.email, groupId = user.group.id!!, id = user.id!!),
                    message = "User found!"
                )
            )
    }

    /**
     * Atualiza dados do usuário — somente o próprio usuário pode alterar seus dados.
     */
    @Transactional
    fun update(body: UserUpdateResponseDto, id: Long): ResponseEntity<ResponseDto<UserResponseDto?>> {
        val user: User? = repository.findByIdOrNull(id)
        val loggedUser = repository.findByEmail(userAuthenticated.getUsernameLogged())

        if (user == null) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                    ResponseDto(
                        code = HttpStatus.NOT_FOUND.value(),
                        data = null,
                        message = "User not found!"
                    )
                )
        }

        if (loggedUser?.id != id) {
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(
                    ResponseDto(
                        code = HttpStatus.FORBIDDEN.value(),
                        data = null,
                        message = "Forbidden! You can only update your own data."
                    )
                )
        }

        user.email = body.email ?: user.email
        user.name = body.name ?: user.name
        repository.save(user)

        return ResponseEntity
            .ok()
            .body(
                ResponseDto(
                    code = HttpStatus.OK.value(),
                    data = UserResponseDto(user.name, email = user.email, groupId = user.group.id!!, id = user.id!!),
                    message = "User updated!"
                )
            )
    }

    /**
     * Deleta usuário — somente o próprio usuário pode se deletar.
     */
    @Transactional
    fun delete(id: Long): ResponseEntity<ResponseDto<UserResponseDto?>> {
        val loggedUser = repository.findByEmail(userAuthenticated.getUsernameLogged())
        val user: User = repository.findByIdOrNull(id) ?: return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(
                ResponseDto(
                    code = HttpStatus.NOT_FOUND.value(),
                    data = null,
                    message = "User not found!"
                )
            )

        if (loggedUser?.id != id) {
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(
                    ResponseDto(
                        code = HttpStatus.FORBIDDEN.value(),
                        data = null,
                        message = "Forbidden! You can only delete your own account."
                    )
                )
        }

        repository.delete(user)

        return ResponseEntity
            .ok()
            .body(
                ResponseDto(
                    code = HttpStatus.OK.value(),
                    data = UserResponseDto(name = user.name, email = user.email, groupId = user.group.id!!, id = user.id!!),
                    message = "User Deleted!"
                )
            )
    }
}