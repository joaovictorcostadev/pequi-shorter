package com.joaovictorcostadev.pequi_short.service

import com.joaovictorcostadev.pequi_short.dto.response.ResponseDto
import com.joaovictorcostadev.pequi_short.dto.user.UserAuthRequestDto
import com.joaovictorcostadev.pequi_short.dto.user.UserAuthResponseDto
import com.joaovictorcostadev.pequi_short.dto.user.UserRequestDto
import com.joaovictorcostadev.pequi_short.dto.user.UserResponseDto
import com.joaovictorcostadev.pequi_short.dto.user.UserUpdateResponseDto
import com.joaovictorcostadev.pequi_short.repository.UserRepository
import org.springframework.stereotype.Service
import com.joaovictorcostadev.pequi_short.entity.User
import com.joaovictorcostadev.pequi_short.enum.GroupEnum
import com.joaovictorcostadev.pequi_short.repository.GroupRepository
import com.joaovictorcostadev.pequi_short.security.UserAuthenticated
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.Instant


@Service
class UserService(
    private val repository: UserRepository,
    private val groupRepository: GroupRepository,
    private val passwordEncoder: PasswordEncoder,
    private val userAuthenticated: UserAuthenticated,
    private val userDetailsService: CustomUserDetailsService,
    private val tokenService: TokenService,
    private val authenticatorManager: AuthenticationManager,

    @Value($$"${jwt.expiration}")
    private val expiration: Long

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
                data = UserResponseDto(name = savedUser.name, email = savedUser.email, groupId = savedUser.group.id!!, id = savedUser.id!!)
            )
        )
    }

    fun get(id: Long) : ResponseEntity<ResponseDto<UserResponseDto?>> {
        val user:User? = repository.findByIdOrNull(id);
        val loggedUser = repository.findByEmail(userAuthenticated.getUsernameLogged())

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

        if(loggedUser?.id != id && loggedUser?.group?.id != GroupEnum.ADMIN.id) {
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(
                    ResponseDto(
                        code = HttpStatus.FORBIDDEN.value(),
                        data = null,
                        message = "Forbidden!"
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

    fun update(body: UserUpdateResponseDto, id:Long) : ResponseEntity<ResponseDto<UserResponseDto?>> {
        val user:User? = repository.findByIdOrNull(id);
        val loggedUser = repository.findByEmail(userAuthenticated.getUsernameLogged())

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

        if(loggedUser?.id != id && loggedUser?.group?.id != GroupEnum.ADMIN.id) {
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(
                    ResponseDto(
                        code = HttpStatus.FORBIDDEN.value(),
                        data = null,
                        message = "Forbidden!"
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
                    data = UserResponseDto(user.name, email = user.email, groupId = user.group.id!!, id = user.id!!),
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
                    data = UserResponseDto(name = user.name, email = user.email, groupId = user.group.id!!, id = user.id!!),
                    message = "User Deleted!"
                    )
            )

    }

    fun auth(userAuthRequest: UserAuthRequestDto) : ResponseEntity<ResponseDto<UserAuthResponseDto>> {
        authenticatorManager.authenticate(
            UsernamePasswordAuthenticationToken(userAuthRequest.email, userAuthRequest.password)
        )

        val userDetails = userDetailsService.loadUserByUsername(userAuthRequest.email)
        val token = tokenService.generateToken(userDetails)

        return ResponseEntity.ok(ResponseDto(
            code = HttpStatus.OK.value(),
            message = "Authorized",
            data = UserAuthResponseDto(
                token = token,
                iat = System.currentTimeMillis(),
                exp = System.currentTimeMillis() + expiration)))

    }

}