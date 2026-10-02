package com.joaovictorcostadev.pequi_short.controller

import com.joaovictorcostadev.pequi_short.dto.response.ResponseDto
import com.joaovictorcostadev.pequi_short.dto.url.UrlDtoRequest
import com.joaovictorcostadev.pequi_short.dto.url.UrlDtoResponse
import com.joaovictorcostadev.pequi_short.service.UrlService
import com.joaovictorcostadev.pequi_short.util.getBrowser
import com.joaovictorcostadev.pequi_short.util.getClientIp
import com.joaovictorcostadev.pequi_short.util.getDeviceType
import com.joaovictorcostadev.pequi_short.util.getOs
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
class UrlController(val urlService: UrlService) {

    @PostMapping("/api/url/save")
    @PreAuthorize("hasAuthority('URL_CREATE')")
    fun saveUrl(@RequestBody urlBody: UrlDtoRequest): ResponseEntity<ResponseDto<UrlDtoResponse>> {
        val data = urlService.save(urlBody)
        return ResponseEntity.ok(ResponseDto(code = HttpStatus.OK.value(), data = data, message = "Url created!"))
    }

    @GetMapping("/r/{name}")
    fun redirectUrl(@PathVariable name: String, request: HttpServletRequest): ResponseEntity<Any> {
        val externalUrl = urlService.redirect(
            name = name,
            ip = request.getClientIp(),
            userAgent = request.getHeader("User-Agent"),
            browser = request.getBrowser(),
            os = request.getOs(),
            deviceType = request.getDeviceType()
        )
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(externalUrl)).build()
    }

    @GetMapping("/api/url/get")
    @PreAuthorize("hasAuthority('URL_GET')")
    fun getUrls(): ResponseEntity<ResponseDto<List<UrlDtoResponse>>> {
        val data = urlService.get()
        return ResponseEntity.ok(ResponseDto(code = HttpStatus.OK.value(), data = data, message = "Urls found!"))
    }

    @DeleteMapping("/api/url/delete/{id}")
    @PreAuthorize("hasAuthority('URL_DELETE')")
    fun delete(@PathVariable id: Long): ResponseEntity<ResponseDto<UrlDtoResponse>> {
        val data = urlService.delete(id)
        return ResponseEntity.ok(ResponseDto(code = HttpStatus.OK.value(), data = data, message = "Url deleted!"))
    }

}