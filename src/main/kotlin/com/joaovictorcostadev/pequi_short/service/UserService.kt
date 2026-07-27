package com.joaovictorcostadev.pequi_short.service

import com.joaovictorcostadev.pequi_short.dto.response.ResponseDto
import com.joaovictorcostadev.pequi_short.dto.user.UserRequestDto
import com.joaovictorcostadev.pequi_short.dto.user.UserResponseDto
import com.joaovictorcostadev.pequi_short.dto.user.UserUpdateResponseDto
import com.joaovictorcostadev.pequi_short.repository.UserRepository
import org.springframework.stereotype.Service
import com.joaovictorcostadev.pequi_short.entity.User
import com.joaovictorcostadev.pequi_short.repository.GroupRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.Instant


@Service
class UserService(
    private val repository: UserRepository,
    private val groupRepository: GroupRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun save(user: UserRequestDto) : ResponseEntity<ResponseDto<UserResponseDto>> {
        val group = groupRepository.findById(user.groupId).orElseThrow{
            RuntimeException("Group not found!")
        }

        val hashedPassword: String = passwordEncoder.encode(user.password).toString();
        val entity: User = User(name = user.name, email = user.email, password = hashedPassword, updatedAt = Instant.now() , group = group)
        val savedUser: User = repository.save(entity)
        return ResponseEntity.ok(
            ResponseDto(
                code = HttpStatus.OK.value(),
                message = "User created",
                data = UserResponseDto(name = savedUser.name, email = savedUser.email,savedUser.group.id!!)
            )
        )
    }

    fun get(id: Long) : ResponseEntity<ResponseDto<UserResponseDto?>> {

        val user:User? = repository.findByIdOrNull(id);

        val authetication = SecurityContextHolder.getContext().authentication

        if(user == null) {
            return ResponseEntity
                .badRequest().
                body(
                    ResponseDto(
                        code = HttpStatus.BAD_REQUEST.value(),
                        data = null,
                        message = "User not found!"
                    )
                )
        }

        return ResponseEntity
            .ok()
            .body(
                ResponseDto(
                    code = HttpStatus.OK.value(),
                    data = UserResponseDto(user.name, email = user.email, groupId = user.group.id!!),
                    message = "User found!"
                )
            )
    }

    fun update(body: UserUpdateResponseDto, id:Long) : ResponseEntity<ResponseDto<UserResponseDto?>> {
        val user:User? = repository.findByIdOrNull(id);

        if(user == null) {
            return ResponseEntity
                .badRequest().
                body(
                    ResponseDto(
                        code = HttpStatus.BAD_REQUEST.value(),
                        data = null,
                        message = "User not found!"
                    )
                )
        }

        user.email = body.email ?: user.email
        user.name = body.name ?:  user.name
        repository.save(user)

        return ResponseEntity
            .ok()
            .body(
                ResponseDto(
                    code = HttpStatus.OK.value(),
                    data = UserResponseDto(user.name, email = user.email, groupId = user.group.id!!),
                    message = "User updated!"
                )
            )

    }

    fun delete(id: Long) : ResponseEntity<ResponseDto<UserResponseDto?>> {
        val user:User? = repository.findByIdOrNull(id);

        if(user == null) {
            return ResponseEntity
                .badRequest().
                body(
                    ResponseDto(
                        code = HttpStatus.BAD_REQUEST.value(),
                        data = null,
                        message = "User not found!"
                    )
                )
        }

        repository.delete(user);

        return ResponseEntity
            .ok()
            .body(
                ResponseDto(
                    code = HttpStatus.OK.value(),
                    data = UserResponseDto(name = user.name, email = user.email, groupId = user.group.id!!),
                    message = "User Deleted!"
                    )
            )

    }

}