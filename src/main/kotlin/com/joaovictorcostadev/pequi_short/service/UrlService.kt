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
                        data = null, message = "")
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

}