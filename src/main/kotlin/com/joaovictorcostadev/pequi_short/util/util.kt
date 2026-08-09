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