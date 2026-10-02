package com.joaovictorcostadev.pequi_short.controller

import com.joaovictorcostadev.pequi_short.dto.group.GroupRequestDto
import com.joaovictorcostadev.pequi_short.dto.group.GroupResponseDto
import com.joaovictorcostadev.pequi_short.dto.group.GroupUpdateRequestDto
import com.joaovictorcostadev.pequi_short.dto.response.ResponseDto
import com.joaovictorcostadev.pequi_short.service.GroupService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class GroupController(private val groupService: GroupService) {

    @PostMapping("api/group/save")
    @PreAuthorize("hasAuthority('GROUP_CREATE')")
    fun save(@Valid @RequestBody groupRequest: GroupRequestDto): ResponseEntity<ResponseDto<GroupResponseDto>> {
        val data = groupService.save(groupRequest)
        return ResponseEntity.ok(ResponseDto(code = HttpStatus.OK.value(), data = data, message = "Created group!"))
    }

    @GetMapping("api/group/{id}")
    @PreAuthorize("hasAuthority('GROUP_GET')")
    fun findById(@PathVariable id: Long): ResponseEntity<ResponseDto<GroupResponseDto>> {
        val data = groupService.get(id)
        return ResponseEntity.ok(ResponseDto(code = HttpStatus.OK.value(), data = data, message = "Group found!"))
    }

    @GetMapping("api/group/")
    @PreAuthorize("hasAuthority('GROUP_GET_ALL')")
    fun getAll(): ResponseEntity<ResponseDto<List<GroupResponseDto>>> {
        val data = groupService.getAll()
        return ResponseEntity.ok(ResponseDto(code = HttpStatus.OK.value(), data = data, message = "Group found!"))
    }

    @PutMapping("api/group/update")
    @PreAuthorize("hasAuthority('GROUP_UPDATE')")
    fun update(@RequestBody requestGroup: GroupUpdateRequestDto): ResponseEntity<ResponseDto<GroupResponseDto>> {
        val data = groupService.update(requestGroup)
        return ResponseEntity.ok(ResponseDto(code = HttpStatus.OK.value(), data = data, message = "Group Updated"))
    }

    @DeleteMapping("api/group/delete/{id}")
    @PreAuthorize("hasAuthority('GROUP_DELETE')")
    fun delete(@PathVariable id: Long): ResponseEntity<ResponseDto<GroupResponseDto>> {
        val data = groupService.delete(id)
        return ResponseEntity.ok(ResponseDto(code = HttpStatus.OK.value(), data = data, message = "Group deleted!"))
    }

}