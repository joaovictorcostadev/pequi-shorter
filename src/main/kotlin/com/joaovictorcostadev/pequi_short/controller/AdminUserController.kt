package com.joaovictorcostadev.pequi_short.controller

import com.joaovictorcostadev.pequi_short.dto.response.ResponseDto
import com.joaovictorcostadev.pequi_short.dto.user.AdminUserRequestDto
import com.joaovictorcostadev.pequi_short.dto.user.UserAuthRequestDto
import com.joaovictorcostadev.pequi_short.dto.user.UserAuthResponseDto
import com.joaovictorcostadev.pequi_short.dto.user.UserLogoutRequest
import com.joaovictorcostadev.pequi_short.dto.user.UserRefreshRequestDto
import com.joaovictorcostadev.pequi_short.dto.user.UserRefreshResponseDto
import com.joaovictorcostadev.pequi_short.dto.user.UserResponseDto
import com.joaovictorcostadev.pequi_short.dto.user.UserUpdateResponseDto
import com.joaovictorcostadev.pequi_short.enum.GroupEnum
import com.joaovictorcostadev.pequi_short.service.AdminUserService
import com.joaovictorcostadev.pequi_short.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/admin/")
class AdminUserController(
    private val adminUserService: AdminUserService,
    private val authService: AuthService
)

{
    @PostMapping("user/auth/save")
    fun save(@Valid @RequestBody userRequest: AdminUserRequestDto) :  ResponseEntity<ResponseDto<UserResponseDto?>> {
        return adminUserService.save(userRequest);
    }


    @PreAuthorize("hasAuthority('ADMIN_USER_GET')")
    @GetMapping("user/{id}")
    fun getById(@PathVariable id: Long) : ResponseEntity<ResponseDto<UserResponseDto?>> {
        return adminUserService.get(id);
    }

    @PreAuthorize(value = "hasAuthority('ADMIN_USER_UPDATE')")
    @PutMapping("user/update/{id}")
    fun updatedById(@Valid @RequestBody body: UserUpdateResponseDto, @PathVariable id: Long) : ResponseEntity<ResponseDto<UserResponseDto?>> {
        return adminUserService.update(body, id)
    }

    @PreAuthorize(value = "hasAuthority('ADMIN_USER_DELETE')")
    @DeleteMapping("user/delete/{id}")
    fun  deleteById(@PathVariable id: Long) : ResponseEntity<ResponseDto<UserResponseDto?>> {
        return adminUserService.delete(id);
    }

    @PostMapping("user/auth/login")
    fun auth(@Valid @RequestBody userAuthRequest: UserAuthRequestDto) : ResponseEntity<ResponseDto<UserAuthResponseDto?>> {
        return  authService.authenticate(userAuthRequest, GroupEnum.USER.id)
    }

    @PostMapping("user/auth/logout")
    fun logout(@Valid @RequestBody userLogoutRequest: UserLogoutRequest) : ResponseEntity<ResponseDto<String?>> {
        return authService.logout(userLogoutRequest)
    }

    @PostMapping("user/auth/refresh")
    fun refreshToken(@Valid @RequestBody userRefreshRequestDto: UserRefreshRequestDto) : ResponseEntity<ResponseDto<UserRefreshResponseDto?>> {
        return  authService.refreshToken(userRefreshRequestDto)
    }
}
