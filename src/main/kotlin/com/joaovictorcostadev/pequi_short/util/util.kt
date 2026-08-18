package com.joaovictorcostadev.pequi_short.util

import jakarta.servlet.http.HttpServletRequest

/**
 * Retorna o IP real do cliente tratando cenários com e sem proxies (Cloudflare, Nginx, Load Balancers).
 */
fun HttpServletRequest.getClientIp(): String {
    val xForwardedFor = this.getHeader("X-Forwarded-For")

    return if (!xForwardedFor.isNullOrBlank()) {
        xForwardedFor.split(",")[0].trim()
    } else {
        this.remoteAddr
    }
}

/**
 * Retornar o Browser
 */
fun HttpServletRequest.getBrowser(): String {
    val userAgent: String = this.getHeader("User-Agent")
    val regex = Regex("Firefox|Chrome|Mobile|Safari|Opera|PostmanRuntime|curl")
    return regex.find(userAgent)?.value ?: "Unknown"
}

/**
 * Retornar o Sistema Operacional
 */
fun HttpServletRequest.getOs(): String {
    val userAgent = this.getHeader("User-Agent") ?: return "Unknown"

    return when {
        userAgent.contains("Windows", ignoreCase = true) -> "Windows"
        userAgent.contains("Android", ignoreCase = true) -> "Android"
        userAgent.contains("iPhone", ignoreCase = true) ||
                userAgent.contains("iPad", ignoreCase = true) -> "iOS"
        userAgent.contains("Macintosh", ignoreCase = true) ||
                userAgent.contains("Mac OS", ignoreCase = true) -> "macOS"
        userAgent.contains("Linux", ignoreCase = true) -> "Linux"
        userAgent.contains("PostmanRuntime", ignoreCase = true) -> "Postman"
        else -> "Unknown"
    }
}
/**
 * Retorna o tipo do dispositivo.
 */
fun HttpServletRequest.getDeviceType(): String {
    val userAgent = this.getHeader("User-Agent") ?: return "Unknown"

    return when {
        Regex("iPad|Tablet|PlayBook|Silk", RegexOption.IGNORE_CASE)
            .containsMatchIn(userAgent) -> "Tablet"

        Regex("Mobile|Android|iPhone|iPod|IEMobile|Opera Mini", RegexOption.IGNORE_CASE)
            .containsMatchIn(userAgent) -> "Mobile"

        Regex("Windows|Macintosh|Mac OS|Linux|X11", RegexOption.IGNORE_CASE)
            .containsMatchIn(userAgent) -> "Desktop"

        Regex("PostmanRuntime", RegexOption.IGNORE_CASE).containsMatchIn(userAgent) -> "Postman"

        else -> "Unknown"
    }
}
