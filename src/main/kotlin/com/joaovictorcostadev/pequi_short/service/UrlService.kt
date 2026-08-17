package com.joaovictorcostadev.pequi_short.service

import com.joaovictorcostadev.pequi_short.dto.geoip.GeoIpDto
import com.joaovictorcostadev.pequi_short.dto.response.ResponseDto
import com.joaovictorcostadev.pequi_short.dto.url.UrlAccessDTO
import com.joaovictorcostadev.pequi_short.dto.url.UrlDtoRequest
import com.joaovictorcostadev.pequi_short.dto.url.UrlDtoResponse
import com.joaovictorcostadev.pequi_short.entity.Url
import com.joaovictorcostadev.pequi_short.entity.User
import com.joaovictorcostadev.pequi_short.enum.GroupEnum
import com.joaovictorcostadev.pequi_short.repository.UrlRepository
import com.joaovictorcostadev.pequi_short.repository.UserRepository
import com.joaovictorcostadev.pequi_short.security.UserAuthenticated
import com.joaovictorcostadev.pequi_short.util.getClientIp
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.net.URI
import java.time.Instant

@Service
class UrlService(
    val repository: UrlRepository,
    val userRepository: UserRepository,
    val userAuthenticated: UserAuthenticated,
    val geoIpService: GeoIpService,
    val urlAccessService: UrlAccessService,


    @Value($$"${host.name}")
    private val hostName: String
) {

    fun save( urlDtoRequest: UrlDtoRequest) : ResponseEntity<ResponseDto<UrlDtoResponse?>> {
        val user: User? = userRepository.findByIdOrNull(urlDtoRequest.userId)
        val loggedUser = userRepository.findByEmail(userAuthenticated.getUsernameLogged())

        if(user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                    ResponseDto(
                        code = HttpStatus.NOT_FOUND.value(),
                        data = null, message = "User not found!")
                )
        }

        if(loggedUser?.id != user.id!! && loggedUser?.group?.id != GroupEnum.ADMIN.id) {
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(
                    ResponseDto(
                        code = HttpStatus.FORBIDDEN.value(),
                        data = null,
                        message = "Forbidden!"
                    )
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
                    data = UrlDtoResponse(id = savedUrl.id!!, name = savedUrl.name, userId = user.id!!, externalUrl = savedUrl.externalUrl, url = "${hostName}/r/${savedUrl.name}"),
                    message = "Url created!")
            )
    }

    fun get() : ResponseEntity<ResponseDto<List<UrlDtoResponse>?>> {
        val loggedUser = userRepository.findByEmail(userAuthenticated.getUsernameLogged())
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                    ResponseDto(
                        code = HttpStatus.NOT_FOUND.value(),
                        data = null, message = "User not found!"
                    )
                )

        val urls = repository.findByUserId(loggedUser.id!!)
            .map {
                UrlDtoResponse(
                    name = it.name,
                    externalUrl = it.externalUrl,
                    userId = it.user.id!!,
                    id = it.id!!,
                    url = "${hostName}/r/${it.name}"
                )
            }

        return ResponseEntity.ok().body(ResponseDto(code = HttpStatus.OK.value(), data = urls, message = "Urls found!"))

    }

    fun redirect(name: String, request: HttpServletRequest) :  ResponseEntity<Any> {

        val url: Url = repository.findByName(name) ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                ResponseDto(
                    code = HttpStatus.NOT_FOUND.value(),
                    data = null, message = ""
                )
            )

        val ip:String = request.getClientIp()
        val geoIp: GeoIpDto = geoIpService.getLocation(ip)
        val urlAccessDTO = UrlAccessDTO(
            userId = url.user.id!!,
            urlId = url.id!!,
            ip = ip,
            state = geoIp.stateName,
            city = geoIp.cityName,
            country = geoIp.countryName,
            userAgent = request.getHeader("User-Agent"),
            browser = request.getHeader("User-Agent"),
            operatingSystem = request.getHeader("User-Agent"),
            deviceType = request.getHeader("User-Agent"),
            referrer = request.getHeader("User-Agent"),
            updatedAt = Instant.now(),
        )

        urlAccessService.save(urlAccessDTO)

        return ResponseEntity
            .status(HttpStatus.FOUND)
            .location(URI.create(url.externalUrl))
            .build()

    }

    fun delete(id:Long) : ResponseEntity<ResponseDto<UrlDtoResponse?>> {
        val url: Url = repository.findByIdOrNull(id)
            ?: return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                    ResponseDto(
                        code = HttpStatus.NOT_FOUND.value(),
                        data = null, message = "Url not found!")
                )

        val user = userRepository.findByIdOrNull(url.user.id!!)
        val loggedUser = userRepository.findByEmail(userAuthenticated.getUsernameLogged())

        if(user == null || loggedUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                    ResponseDto(
                        code = HttpStatus.NOT_FOUND.value(),
                        data = null, message = "User not found!")
                )
        }

        if(loggedUser.id != user.id!! && loggedUser.group.id != GroupEnum.ADMIN.id) {
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(
                    ResponseDto(
                        code = HttpStatus.FORBIDDEN.value(),
                        data = null,
                        message = "Forbidden!"
                    )
                )
        }

        repository.delete(url)

        return ResponseEntity.ok()
            .body(
                ResponseDto(
                    code = HttpStatus.OK.value(),
                    data = UrlDtoResponse(
                        name = url.name,
                        externalUrl = url.externalUrl,
                        id = url.id!!,
                        userId = url.user.id!!,
                        url = "${hostName}/r/${url.name}"
                        ),
                    message = "Url deleted!")
            )

    }

}