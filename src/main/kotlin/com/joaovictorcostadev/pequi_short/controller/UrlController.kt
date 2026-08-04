package com.joaovictorcostadev.pequi_short.controller

import com.joaovictorcostadev.pequi_short.dto.response.ResponseDto
import com.joaovictorcostadev.pequi_short.dto.url.UrlDtoRequest
import com.joaovictorcostadev.pequi_short.dto.url.UrlDtoResponse
import com.joaovictorcostadev.pequi_short.service.UrlService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UrlController (val urlService: UrlService) {

    @PostMapping("/api/url/save")
    @PreAuthorize("hasAuthority('URL_CREATE')")
    fun saveUrl(@RequestBody urlBody: UrlDtoRequest) : ResponseEntity<ResponseDto<UrlDtoResponse?>> {
        return  urlService.save(urlBody)
    }

    @GetMapping("/r/{name}")
    fun redirectUrl(@PathVariable name: String) : ResponseEntity<Any>
    {
        return urlService.redirect(name)
    }
}