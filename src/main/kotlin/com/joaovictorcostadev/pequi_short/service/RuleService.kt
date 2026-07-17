package com.joaovictorcostadev.pequi_short.service

import com.joaovictorcostadev.pequi_short.dto.response.ResponseDto
import com.joaovictorcostadev.pequi_short.dto.rule.RuleRequestDto
import com.joaovictorcostadev.pequi_short.dto.rule.RuleResponseDto
import com.joaovictorcostadev.pequi_short.entity.Rule
import com.joaovictorcostadev.pequi_short.repository.RuleRepository
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
}