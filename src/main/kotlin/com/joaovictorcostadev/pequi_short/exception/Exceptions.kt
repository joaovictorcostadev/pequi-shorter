package com.joaovictorcostadev.pequi_short.exception

import org.springframework.http.HttpStatus

open class BusinessException(
    override val message: String,
    val status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR
) : RuntimeException(message)

class NotFoundException(message: String = "Resource not found!") :
    BusinessException(message, HttpStatus.NOT_FOUND)

class ForbiddenException(message: String = "Forbidden!") :
    BusinessException(message, HttpStatus.FORBIDDEN)

class BadRequestException(message: String = "Bad request!") :
    BusinessException(message, HttpStatus.BAD_REQUEST)

class InternalServerException(message: String = "Internal server error!") :
    BusinessException(message, HttpStatus.INTERNAL_SERVER_ERROR)
