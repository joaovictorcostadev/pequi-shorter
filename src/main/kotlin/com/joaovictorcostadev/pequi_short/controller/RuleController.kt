package com.joaovictorcostadev.pequi_short.controller

import com.joaovictorcostadev.pequi_short.dto.response.ResponseDto
import com.joaovictorcostadev.pequi_short.dto.rule.RuleRequestDto
import com.joaovictorcostadev.pequi_short.dto.rule.RuleResponseDto
import com.joaovictorcostadev.pequi_short.service.RuleService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class RuleController (val ruleService: RuleService) {

    @PostMapping("api/rule/save")
    fun save(@Valid @RequestBody ruleRequest: RuleRequestDto ) :  ResponseEntity<ResponseDto<RuleResponseDto>> {
        return ruleService.save(ruleRequest);
    }

}