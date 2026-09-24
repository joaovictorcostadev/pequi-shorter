package com.joaovictorcostadev.pequi_short.service

import com.joaovictorcostadev.pequi_short.dto.response.ResponseDto
import com.joaovictorcostadev.pequi_short.dto.user.AdminUserRequestDto
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
class AdminUserService (
    private val repository: UserRepository,
    private val groupRepository: GroupRepository,
    private val passwordEncoder: PasswordEncoder,
    private val userAuthenticated: UserAuthenticated,
) {
    @Transactional
    fun save(user: AdminUserRequestDto) : ResponseEntity<ResponseDto<UserResponseDto?>> {

        val group: Group = groupRepository.findByIdOrNull(user.groupId)
            ?: return ResponseEntity(ResponseDto(code = HttpStatus.NOT_FOUND.value(), data = null, message = "Group not found!"), HttpStatus.NOT_FOUND)

        val hashedPassword: String = passwordEncoder.encode(user.password).toString()
        val entity = User(name = user.name, email = user.email, password = hashedPassword, updatedAt = Instant.now() , group = group)
        val savedUser: User = repository.save(entity)

        return ResponseEntity.ok(
            ResponseDto(
                code = HttpStatus.OK.value(),
                message = "User created",
                data = UserResponseDto(name = savedUser.name, email = savedUser.email, groupId = savedUser.group.id!!, id = savedUser.id!!)
            )
        )
    }

    fun get(id: Long) : ResponseEntity<ResponseDto<UserResponseDto?>> {
        val user:User = repository.findByIdOrNull(id) ?:
        return ResponseEntity
            .badRequest().
            body(
                ResponseDto(
                    code = HttpStatus.BAD_REQUEST.value(),
                    data = null,
                    message = "User not found!"
                )
            )

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

    @Transactional
    fun update(body: UserUpdateResponseDto, id:Long) : ResponseEntity<ResponseDto<UserResponseDto?>> {
        val user:User = repository.findByIdOrNull(id) ?:
            return ResponseEntity
                .badRequest().
                body(
                    ResponseDto(
                        code = HttpStatus.BAD_REQUEST.value(),
                        data = null,
                        message = "User not found!"
                    )
                )

        user.email = body.email ?: user.email
        user.name = body.name ?:  user.name
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

    @Transactional
    fun delete(id: Long) : ResponseEntity<ResponseDto<UserResponseDto?>> {
        val user: User = repository.findByIdOrNull(id) ?: return ResponseEntity
            .badRequest().body(
                ResponseDto(
                    code = HttpStatus.BAD_REQUEST.value(),
                    data = null,
                    message = "User not found!"
                )
            )

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