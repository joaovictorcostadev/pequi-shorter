package com.joaovictorcostadev.pequi_short.service

import com.joaovictorcostadev.pequi_short.dto.rule.RuleRequestDto
import com.joaovictorcostadev.pequi_short.dto.rule.RuleResponseDto
import com.joaovictorcostadev.pequi_short.dto.rule.RuleUpdateRequestDto
import com.joaovictorcostadev.pequi_short.entity.Rule
import com.joaovictorcostadev.pequi_short.exception.NotFoundException
import com.joaovictorcostadev.pequi_short.repository.RuleRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class RuleService(private val ruleRepository: RuleRepository) {

    fun save(ruleRequest: RuleRequestDto): RuleResponseDto {
        val saved = ruleRepository.save(Rule(name = ruleRequest.name))
        return RuleResponseDto(id = requireNotNull(saved.id), name = saved.name)
    }

    fun getAll(): List<RuleResponseDto> {
        return ruleRepository.findAll()
            .map { RuleResponseDto(id = it.id!!, name = it.name) }
    }

    fun getById(id: Long): RuleResponseDto {
        val rule = ruleRepository.findByIdOrNull(id)
            ?: throw NotFoundException("Rule not found")

        return RuleResponseDto(id = rule.id!!, name = rule.name)
    }

    fun update(ruleUpdateRequestDto: RuleUpdateRequestDto): RuleResponseDto {
        val rule = ruleRepository.findByIdOrNull(ruleUpdateRequestDto.id)
            ?: throw NotFoundException("Rule not found")

        rule.name = ruleUpdateRequestDto.name
        ruleRepository.save(rule)

        return RuleResponseDto(id = rule.id!!, name = rule.name)
    }

    fun delete(id: Long): RuleResponseDto {
        val rule = ruleRepository.findByIdOrNull(id)
            ?: throw NotFoundException("Rule not found")

        ruleRepository.delete(rule)

        return RuleResponseDto(id = rule.id!!, name = rule.name)
    }
}