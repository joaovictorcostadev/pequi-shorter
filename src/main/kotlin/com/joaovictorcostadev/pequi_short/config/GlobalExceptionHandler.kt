package com.joaovictorcostadev.pequi_short.config

import com.joaovictorcostadev.pequi_short.dto.response.ResponseDto
import io.jsonwebtoken.security.SignatureException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
     fun handleValidation(ex: MethodArgumentNotValidException) : ResponseEntity<ResponseDto<Map<String, String>?>> {

        return ResponseEntity.badRequest().body(ResponseDto(
            code = HttpStatus.BAD_REQUEST.value(),
            data = null,
            message = "Validation error: ${ex.bindingResult.fieldError?.field ?: "Field"} : ${ex.bindingResult.fieldError?.defaultMessage ?: "Invalid"}")
        )
    }

    @ExceptionHandler(Exception::class)
     fun handleException(ex: Exception) : ResponseEntity<ResponseDto<Map<String, String>?>> {
        ex.printStackTrace()

        return ResponseEntity.internalServerError().body(
            ResponseDto(code = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                data = null,
                message = "Server Error: ${ex.message ?: "Generic Error"}"))
    }

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadRequestCredentials(ex: Exception) : ResponseEntity<ResponseDto<Map<String, String>?>> {
        ex.printStackTrace()

        return ResponseEntity.badRequest().body(
            ResponseDto(code = HttpStatus.BAD_REQUEST.value(),
                data = null,
                message = "Bad Request: Invalid username or password!"))
    }

    @ExceptionHandler(SignatureException::class)
    fun handleSignatureException(ex: SignatureException) : ResponseEntity<ResponseDto<Map<String, String>?>> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            ResponseDto(
                code = HttpStatus.FORBIDDEN.value(),
                data = null,
                message = "Unauthorized"))

    }
}