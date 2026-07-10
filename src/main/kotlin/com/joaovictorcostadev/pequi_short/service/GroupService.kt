package com.joaovictorcostadev.pequi_short.service

import com.joaovictorcostadev.pequi_short.dto.group.GroupRequestDto
import com.joaovictorcostadev.pequi_short.dto.group.GroupResponseDto
import com.joaovictorcostadev.pequi_short.dto.response.ResponseDto
import com.joaovictorcostadev.pequi_short.entity.Group
import com.joaovictorcostadev.pequi_short.repository.GroupRepository
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
                    message = "Grupo criado!",
                    data = GroupResponseDto(id = requireNotNull(entitySaved.id), name = entitySaved.name)
                )
            )
    }

}