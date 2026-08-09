package com.joaovictorcostadev.pequi_short.service

import com.joaovictorcostadev.pequi_short.dto.url.UrlAccessDTO
import com.joaovictorcostadev.pequi_short.entity.Url
import com.joaovictorcostadev.pequi_short.entity.UrlAccess
import com.joaovictorcostadev.pequi_short.entity.User
import com.joaovictorcostadev.pequi_short.repository.UrlAccessRepository
import com.joaovictorcostadev.pequi_short.repository.UrlRepository
import com.joaovictorcostadev.pequi_short.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.Instant


@Service
class UrlAccessService (
    val repository: UrlAccessRepository,
    val userRepository: UserRepository,
    val urlRepository: UrlRepository
) {

    fun save(urlAccess: UrlAccessDTO) {

        val user: User = userRepository.findByIdOrNull(urlAccess.userId) ?: return
        val url: Url = urlRepository.findByIdOrNull(urlAccess.urlId) ?: return

        repository.save(
            UrlAccess(
                user = user,
                ip = urlAccess.ip,
                country = urlAccess.country,
                state = urlAccess.state,
                city = urlAccess.city,
                deviceType = urlAccess.deviceType,
                operatingSystem = urlAccess.operatingSystem,
                browser = urlAccess.browser,
                userAgent = urlAccess.userAgent,
                url = url,
                updatedAt = Instant.now()
            )
        )

    }
}