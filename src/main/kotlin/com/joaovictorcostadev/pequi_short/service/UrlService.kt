package com.joaovictorcostadev.pequi_short.service

import com.joaovictorcostadev.pequi_short.component.UniqueCodeGenerator
import com.joaovictorcostadev.pequi_short.dto.geoip.GeoIpDto
import com.joaovictorcostadev.pequi_short.dto.url.UrlAccessDTO
import com.joaovictorcostadev.pequi_short.dto.url.UrlDtoRequest
import com.joaovictorcostadev.pequi_short.dto.url.UrlDtoResponse
import com.joaovictorcostadev.pequi_short.entity.Url
import com.joaovictorcostadev.pequi_short.enum.GroupEnum
import com.joaovictorcostadev.pequi_short.exception.BadRequestException
import com.joaovictorcostadev.pequi_short.exception.ForbiddenException
import com.joaovictorcostadev.pequi_short.exception.NotFoundException
import com.joaovictorcostadev.pequi_short.repository.UrlRepository
import com.joaovictorcostadev.pequi_short.repository.UserRepository
import com.joaovictorcostadev.pequi_short.security.UserAuthenticated
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class UrlService(
    val repository: UrlRepository,
    val userRepository: UserRepository,
    val userAuthenticated: UserAuthenticated,
    val geoIpService: GeoIpService,
    val urlAccessService: UrlAccessService,
    val uniqueCodeGenerator: UniqueCodeGenerator,

    @Value("\${host.name}")
    private val hostName: String
) {

    fun save(urlDtoRequest: UrlDtoRequest): UrlDtoResponse {
        val user = userRepository.findByIdOrNull(urlDtoRequest.userId)
            ?: throw NotFoundException("User not found!")

        val loggedUser = userRepository.findByEmail(userAuthenticated.getUsernameLogged())

        if (loggedUser?.id != user.id!! && loggedUser?.group?.id != GroupEnum.ADMIN.id) {
            throw ForbiddenException()
        }

        urlDtoRequest.name = urlDtoRequest.name ?: uniqueCodeGenerator.getUniqueCode(8)

        val urlExist = repository.findByName(urlDtoRequest.name!!)
        if (urlExist != null) {
            throw BadRequestException("Url existed!")
        }

        val savedUrl = repository.save(Url(
            name = urlDtoRequest.name!!,
            externalUrl = urlDtoRequest.externalUrl,
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
            user = user,
        ))

        return UrlDtoResponse(
            id = savedUrl.id!!,
            name = savedUrl.name,
            userId = user.id!!,
            externalUrl = savedUrl.externalUrl,
            url = "${hostName}/r/${savedUrl.name}"
        )
    }

    fun get(): List<UrlDtoResponse> {
        val loggedUser = userRepository.findByEmail(userAuthenticated.getUsernameLogged())
            ?: throw NotFoundException("User not found!")

        return repository.findByUserId(loggedUser.id!!)
            .map {
                UrlDtoResponse(
                    name = it.name,
                    externalUrl = it.externalUrl,
                    userId = it.user.id!!,
                    id = it.id!!,
                    url = "${hostName}/r/${it.name}"
                )
            }
    }

    /**
     * Processa o redirecionamento: registra o acesso e retorna a URL externa.
     * O controller é responsável por extrair os dados do HttpServletRequest
     * e por montar o ResponseEntity de redirect.
     */
    fun redirect(name: String, ip: String, userAgent: String?, browser: String, os: String, deviceType: String): String {
        val url = repository.findByName(name)
            ?: throw NotFoundException()

        val geoIp: GeoIpDto = geoIpService.getLocation(ip)

        urlAccessService.save(UrlAccessDTO(
            userId = url.user.id!!,
            urlId = url.id!!,
            ip = ip,
            state = geoIp.stateName,
            city = geoIp.cityName,
            country = geoIp.countryName,
            userAgent = userAgent,
            browser = browser,
            operatingSystem = os,
            deviceType = deviceType,
            updatedAt = Instant.now(),
        ))

        return url.externalUrl
    }

    fun delete(id: Long): UrlDtoResponse {
        val url = repository.findByIdOrNull(id)
            ?: throw NotFoundException("Url not found!")

        val user = userRepository.findByIdOrNull(url.user.id!!)
            ?: throw NotFoundException("User not found!")

        val loggedUser = userRepository.findByEmail(userAuthenticated.getUsernameLogged())
            ?: throw NotFoundException("User not found!")

        if (loggedUser.id != user.id!! && loggedUser.group.id != GroupEnum.ADMIN.id) {
            throw ForbiddenException()
        }

        repository.delete(url)

        return UrlDtoResponse(
            name = url.name,
            externalUrl = url.externalUrl,
            id = url.id!!,
            userId = url.user.id!!,
            url = "${hostName}/r/${url.name}"
        )
    }

}