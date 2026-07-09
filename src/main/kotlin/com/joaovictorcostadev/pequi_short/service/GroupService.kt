package com.joaovictorcostadev.pequi_short.service

import com.joaovictorcostadev.pequi_short.dto.group.GroupRequestDto
import com.joaovictorcostadev.pequi_short.dto.group.GroupResponseDto
import com.joaovictorcostadev.pequi_short.entity.Group
import com.joaovictorcostadev.pequi_short.repository.GroupRepository
import org.springframework.stereotype.Service

@Service
class GroupService(val repository: GroupRepository) {

    fun save(group: GroupRequestDto) : GroupResponseDto {
        val entitySaved = repository.save(Group(name = group.name))
        return GroupResponseDto(id = requireNotNull(entitySaved.id), name = entitySaved.name)
    }

}