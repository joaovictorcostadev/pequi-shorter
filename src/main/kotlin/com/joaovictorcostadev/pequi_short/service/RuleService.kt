package com.joaovictorcostadev.pequi_short.service

import com.joaovictorcostadev.pequi_short.dto.response.ResponseDto
import com.joaovictorcostadev.pequi_short.dto.rule.RuleRequestDto
import com.joaovictorcostadev.pequi_short.dto.rule.RuleResponseDto
import com.joaovictorcostadev.pequi_short.dto.rule.RuleUpdateRequestDto
import com.joaovictorcostadev.pequi_short.entity.Rule
import com.joaovictorcostadev.pequi_short.repository.RuleRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class RuleService (private val ruleRepository: RuleRepository)  {

    fun save(ruleRequest: RuleRequestDto) :  ResponseEntity<ResponseDto<RuleResponseDto>> {

        val rule = Rule(name = ruleRequest.name)
        val saved: Rule = ruleRepository.save(rule)

        return ResponseEntity.ok().body(
            ResponseDto(
                code = HttpStatus.OK.value(),
                data = RuleResponseDto(id = requireNotNull(saved.id), name = saved.name),
                message = "Rule created!")
        )
    }

    fun getAll() : ResponseEntity<ResponseDto<List<RuleResponseDto>>> {
        val rules: List<RuleResponseDto> = ruleRepository
            .findAll()
            .map { RuleResponseDto(id = it.id!!, name = it.name) }
            .toList()

        return ResponseEntity.ok().body(ResponseDto(code = HttpStatus.OK.value(), data = rules, message = "Rules Found"))
    }

    fun getById(id: Long) : ResponseEntity<ResponseDto<RuleResponseDto?>> {
        val rule = ruleRepository.findByIdOrNull(id) ?:
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                ResponseDto(
                    code = HttpStatus.NOT_FOUND.value(),
                    data = null,
                    message = "Rule not found"
                )
            )
        return ResponseEntity.ok()
            .body(
                ResponseDto(
                    code = HttpStatus.OK.value(),
                    data = RuleResponseDto(
                        id = rule.id!!,
                        name = rule.name
                    ),
                    message = "Rules Found"
                )
            )
    }

    fun update(ruleUpdateRequestDto: RuleUpdateRequestDto) : ResponseEntity<ResponseDto<RuleResponseDto?>> {
        val rule = ruleRepository.findByIdOrNull(ruleUpdateRequestDto.id) ?:
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                ResponseDto(
                    code = HttpStatus.NOT_FOUND.value(),
                    data = null,
                    message = "Rule not found"
                )
            )

        rule.name = ruleUpdateRequestDto.name
        ruleRepository.save(rule)

        return ResponseEntity
            .ok()
            .body(
                ResponseDto(
                    code = HttpStatus.OK.value(),
                    data = RuleResponseDto(id = rule.id!!, name = rule.name),
                    message = "Rule updated")
            )
    }

    fun delete (id: Long) : ResponseEntity<ResponseDto<RuleResponseDto?>> {
        val rule = ruleRepository.findByIdOrNull(id) ?:
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                ResponseDto(
                    code = HttpStatus.NOT_FOUND.value(),
                    data = null,
                    message = "Rule not found"
                )
            )

        ruleRepository.delete(rule)

        return ResponseEntity.ok().body(
            ResponseDto(
                code = HttpStatus.OK.value(),
                data = RuleResponseDto(rule.id!!, name = rule.name),
                message = "Rule deleted")
        )

    }
}