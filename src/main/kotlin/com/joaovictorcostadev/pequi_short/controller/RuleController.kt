package com.joaovictorcostadev.pequi_short.controller

import com.joaovictorcostadev.pequi_short.dto.response.ResponseDto
import com.joaovictorcostadev.pequi_short.dto.rule.RuleRequestDto
import com.joaovictorcostadev.pequi_short.dto.rule.RuleResponseDto
import com.joaovictorcostadev.pequi_short.dto.rule.RuleUpdateRequestDto
import com.joaovictorcostadev.pequi_short.service.RuleService
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
class RuleController(val ruleService: RuleService) {

    @PreAuthorize("hasAuthority('RULE_CREATE')")
    @PostMapping("api/rule/save")
    fun save(@Valid @RequestBody ruleRequest: RuleRequestDto): ResponseEntity<ResponseDto<RuleResponseDto>> {
        val data = ruleService.save(ruleRequest)
        return ResponseEntity.ok(ResponseDto(code = HttpStatus.OK.value(), data = data, message = "Rule created!"))
    }

    @PreAuthorize("hasAuthority('RULE_GET_ALL')")
    @GetMapping("api/rule/getAll")
    fun getAll(): ResponseEntity<ResponseDto<List<RuleResponseDto>>> {
        val data = ruleService.getAll()
        return ResponseEntity.ok(ResponseDto(code = HttpStatus.OK.value(), data = data, message = "Rules Found"))
    }

    @PreAuthorize("hasAuthority('RULE_GET')")
    @GetMapping("api/rule/{id}")
    fun get(@PathVariable id: Long): ResponseEntity<ResponseDto<RuleResponseDto>> {
        val data = ruleService.getById(id)
        return ResponseEntity.ok(ResponseDto(code = HttpStatus.OK.value(), data = data, message = "Rules Found"))
    }

    @PreAuthorize("hasAuthority('RULE_UPDATE')")
    @PutMapping("api/rule/update")
    fun update(@RequestBody ruleRequest: RuleUpdateRequestDto): ResponseEntity<ResponseDto<RuleResponseDto>> {
        val data = ruleService.update(ruleRequest)
        return ResponseEntity.ok(ResponseDto(code = HttpStatus.OK.value(), data = data, message = "Rule updated"))
    }

    @PreAuthorize("hasAuthority('RULE_DELETE')")
    @DeleteMapping("api/rule/delete/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<ResponseDto<RuleResponseDto>> {
        val data = ruleService.delete(id)
        return ResponseEntity.ok(ResponseDto(code = HttpStatus.OK.value(), data = data, message = "Rule deleted"))
    }
}