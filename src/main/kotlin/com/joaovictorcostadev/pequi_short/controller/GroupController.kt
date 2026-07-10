package com.joaovictorcostadev.pequi_short.controller

import com.joaovictorcostadev.pequi_short.dto.group.GroupRequestDto
import com.joaovictorcostadev.pequi_short.dto.group.GroupResponseDto
import com.joaovictorcostadev.pequi_short.service.GroupService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import jakarta.validation.Valid

@RestController
class GroupController(private val groupService: GroupService) {

    @PostMapping("api/group/save")
    fun save(@Valid @RequestBody groupRequest: GroupRequestDto) : GroupResponseDto {
        return groupService.save(groupRequest);
    }
}