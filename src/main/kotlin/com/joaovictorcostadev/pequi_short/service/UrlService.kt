package com.joaovictorcostadev.pequi_short.service

import com.joaovictorcostadev.pequi_short.dto.response.ResponseDto
import com.joaovictorcostadev.pequi_short.dto.url.UrlDtoRequest
import com.joaovictorcostadev.pequi_short.dto.url.UrlDtoResponse
import com.joaovictorcostadev.pequi_short.entity.Url
import com.joaovictorcostadev.pequi_short.entity.User
import com.joaovictorcostadev.pequi_short.repository.UrlRepository
import com.joaovictorcostadev.pequi_short.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.net.URI
import java.time.Instant

@Service
class UrlService(
    val repository: UrlRepository,
    val userRepository: UserRepository
) {

    fun save( urlDtoRequest: UrlDtoRequest) : ResponseEntity<ResponseDto<UrlDtoResponse?>> {
        val user: User? = userRepository.findByIdOrNull(urlDtoRequest.userId)

        if(user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                    ResponseDto(
                        code = HttpStatus.NOT_FOUND.value(),
                        data = null, message = "User not found!")
                )
        }

        val urlExist: Url? = repository.findByName(urlDtoRequest.name)

        if(urlExist != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                    ResponseDto(
                        code = HttpStatus.BAD_REQUEST.value(),
                        data = null, message = "Url existed!")
                )
        }

        val savedUrl = repository.save(Url(
            name = urlDtoRequest.name,
            externalUrl = urlDtoRequest.externalUrl,
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
            user = user,
        ))

        return ResponseEntity.ok()
            .body(
                ResponseDto(
                    code = HttpStatus.OK.value(),
                    data = UrlDtoResponse(id = savedUrl.id!!, name = savedUrl.name, userId = user.id!!, externalUrl = savedUrl.externalUrl),
                    message = "Url created!")
            )
    }

    fun redirect(name: String) :  ResponseEntity<Any> {

        val url:Url? = repository.findByName(name);

        if(url == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                    ResponseDto(
                        code = HttpStatus.NOT_FOUND.value(),
                        data = null, message = "")
                )
        }


        return ResponseEntity
            .status(HttpStatus.FOUND)
            .location(URI.create(url.externalUrl))
            .build()

    }

}