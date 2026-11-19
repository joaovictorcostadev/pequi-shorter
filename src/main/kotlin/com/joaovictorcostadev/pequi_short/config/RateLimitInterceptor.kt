package com.joaovictorcostadev.pequi_short.config

import com.joaovictorcostadev.pequi_short.service.RateLimitService
import com.joaovictorcostadev.pequi_short.util.getClientIp
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class RateLimitInterceptor(
    private val rateLimitService: RateLimitService
) : HandlerInterceptor {


    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val ip: String = request.getClientIp()
        val resolveBucketForIp = rateLimitService.resolveBucket(ip)
        if (!resolveBucketForIp.tryConsume(1)) {
            response.status = HttpStatus.TOO_MANY_REQUESTS.value() // 429
            response.contentType = "application/json"
            response.writer.write("""{"message": "Too many requests. Try again later."}""")
            return false
        }
        return true
    }

}

