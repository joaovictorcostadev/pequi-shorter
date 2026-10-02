package com.joaovictorcostadev.pequi_short.service

import com.joaovictorcostadev.pequi_short.dto.group.GroupRequestDto
import com.joaovictorcostadev.pequi_short.dto.group.GroupResponseDto
import com.joaovictorcostadev.pequi_short.dto.group.GroupUpdateRequestDto
import com.joaovictorcostadev.pequi_short.entity.Group
import com.joaovictorcostadev.pequi_short.exception.NotFoundException
import com.joaovictorcostadev.pequi_short.repository.GroupRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class GroupService(val repository: GroupRepository) {

    fun save(group: GroupRequestDto): GroupResponseDto {
        val entitySaved = repository.save(Group(name = group.name))
        return GroupResponseDto(
            id = requireNotNull(entitySaved.id),
            name = entitySaved.name
        )
    }

    fun get(id: Long): GroupResponseDto {
        val group = repository.findByIdOrNull(id)
            ?: throw NotFoundException("Group not found!")

        return GroupResponseDto(
            id = group.id!!,
            name = group.name
        )
    }

    fun getAll(): List<GroupResponseDto> {
        return repository.findAll()
            .map { GroupResponseDto(id = it.id!!, name = it.name) }
    }

    fun update(groupRequest: GroupUpdateRequestDto): GroupResponseDto {
        val group = repository.findByIdOrNull(groupRequest.id)
            ?: throw NotFoundException("Group not found!")

        group.name = groupRequest.name
        repository.save(group)

        return GroupResponseDto(
            id = groupRequest.id,
            name = groupRequest.name
        )
    }

    fun delete(id: Long): GroupResponseDto {
        val group = repository.findByIdOrNull(id)
            ?: throw NotFoundException("Group not found!")

        repository.delete(group)

        return GroupResponseDto(
            id = group.id!!,
            name = group.name
        )
    }

}