package com.joaovictorcostadev.pequi_short.controller

import com.joaovictorcostadev.pequi_short.dto.response.ResponseDto
import com.joaovictorcostadev.pequi_short.dto.rule.RuleRequestDto
import com.joaovictorcostadev.pequi_short.dto.rule.RuleResponseDto
import com.joaovictorcostadev.pequi_short.dto.rule.RuleUpdateRequestDto
import com.joaovictorcostadev.pequi_short.service.RuleService
import jakarta.validation.Valid
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
class RuleController (val ruleService: RuleService) {

    @PreAuthorize("hasAuthority('RULE_CREATE')")
    @PostMapping("api/rule/save")
    fun save(@Valid @RequestBody ruleRequest: RuleRequestDto): ResponseEntity<ResponseDto<RuleResponseDto>> {
        return ruleService.save(ruleRequest);
    }

    @PreAuthorize("hasAuthority('RULE_GET_ALL')")
    @GetMapping("api/rule/getAll")
    open fun getAll(): ResponseEntity<ResponseDto<List<RuleResponseDto>>> {
        return ruleService.getAll()
    }

    @PreAuthorize("hasAuthority('RULE_GET')")
    @GetMapping("api/rule/{id}")
    open fun get(@PathVariable id: Long): ResponseEntity<ResponseDto<RuleResponseDto?>> {
        return ruleService.getById(id)
    }

    @PreAuthorize("hasAuthority('RULE_UPDATE')")
    @PutMapping("api/rule/update")
    open fun update(@RequestBody ruleRequest: RuleUpdateRequestDto): ResponseEntity<ResponseDto<RuleResponseDto?>> {
        return ruleService.update(ruleRequest)
    }


    @PreAuthorize("hasAuthority('RULE_GET')")
    @DeleteMapping("api/rule/delete/{id}")
    open fun delete(@PathVariable id: Long): ResponseEntity<ResponseDto<RuleResponseDto?>> {
        return ruleService.delete(id)
    }
}