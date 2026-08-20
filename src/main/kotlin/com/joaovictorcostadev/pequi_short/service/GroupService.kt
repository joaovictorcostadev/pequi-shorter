package com.joaovictorcostadev.pequi_short.service

import com.joaovictorcostadev.pequi_short.dto.group.GroupRequestDto
import com.joaovictorcostadev.pequi_short.dto.group.GroupResponseDto
import com.joaovictorcostadev.pequi_short.dto.group.GroupUpdateRequestDto
import com.joaovictorcostadev.pequi_short.dto.response.ResponseDto
import com.joaovictorcostadev.pequi_short.entity.Group
import com.joaovictorcostadev.pequi_short.repository.GroupRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class GroupService(val repository: GroupRepository) {

    fun save(group: GroupRequestDto) : ResponseEntity<ResponseDto<GroupResponseDto>> {
        val entitySaved = repository.save(Group(name = group.name))
        return ResponseEntity.ok()
            .body(
                ResponseDto(
                    code = HttpStatus.OK.value(),
                    message = "Created group!",
                    data = GroupResponseDto(id = requireNotNull(entitySaved.id), name = entitySaved.name)
                )
            )
    }

    fun get( id: Long) : ResponseEntity<ResponseDto<GroupResponseDto?>> {

        val group: Group = repository.findByIdOrNull(id)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseDto(
                    code = HttpStatus.NOT_FOUND.value(),
                    data = null,
                    message = "")
                )

        return ResponseEntity.ok().
        body(
            ResponseDto(
                code = HttpStatus.OK.value(),
                data = GroupResponseDto(
                    id = group.id!!,
                    name = group.name),
                message = "Group found!"
            )
        )

    }

    fun getAll() : ResponseEntity<ResponseDto<List<GroupResponseDto?>>> {

        val groups: List<GroupResponseDto> = repository.findAll()
            .map { GroupResponseDto(id = it.id!!, it.name) }

        return ResponseEntity.ok().
        body(
            ResponseDto(
                code = HttpStatus.OK.value(),
                data = groups,
                message = "Group found!"
            )
        )

    }

    fun update(groupRequest: GroupUpdateRequestDto) : ResponseEntity<ResponseDto<GroupResponseDto?>> {

        val group: Group = repository.findByIdOrNull(groupRequest.id)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseDto(
                    HttpStatus.NOT_FOUND.value(),
                    message = "Group not found!",
                    data = null)
                )
        group.name = groupRequest.name
        repository.save(group)

        return ResponseEntity
            .ok(
                ResponseDto(
                    code = HttpStatus.OK.value(),
                    message = "Group Updated",
                    data = GroupResponseDto(id = groupRequest.id, name = groupRequest.name),
                    )
            )
    }


}