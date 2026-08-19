package com.joaovictorcostadev.pequi_short.controller

import com.joaovictorcostadev.pequi_short.dto.group.GroupRequestDto
import com.joaovictorcostadev.pequi_short.dto.group.GroupResponseDto
import com.joaovictorcostadev.pequi_short.dto.group.GroupUpdateRequestDto
import com.joaovictorcostadev.pequi_short.dto.response.ResponseDto
import com.joaovictorcostadev.pequi_short.service.GroupService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping

@RestController
class GroupController(private val groupService: GroupService) {

    @PostMapping("api/group/save")
    @PreAuthorize("hasAuthority('GROUP_CREATE')")
    fun save(@Valid @RequestBody groupRequest: GroupRequestDto) : ResponseEntity<ResponseDto<GroupResponseDto>> {
        return groupService.save(groupRequest);
    }

    @GetMapping("api/group/{id}")
    @PreAuthorize("hasAuthority('GROUP_CREATE')")
    fun findById(@PathVariable id: Long) : ResponseEntity<ResponseDto<GroupResponseDto?>> {
        return groupService.get(id);
    }

}