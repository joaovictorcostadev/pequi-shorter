package com.joaovictorcostadev.pequi_short.config

import com.joaovictorcostadev.pequi_short.dto.response.ResponseDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
     fun handleValidation(ex: MethodArgumentNotValidException) : ResponseEntity<ResponseDto<Map<String, String>?>> {

        println(ex.bindingResult.fieldErrors.toString());

        val errors: Map<String, String> = ex.bindingResult.fieldErrors.associate {
            it.field to (it.defaultMessage ?: "Inválido")
        }


        return ResponseEntity.badRequest().body(ResponseDto(
            code = HttpStatus.BAD_REQUEST.value(),
            data = null,
            message = "Erro de validação: ${ex.bindingResult.fieldError?.field ?: "Campo"} : ${ex.bindingResult.fieldError?.defaultMessage ?: "Inválido"}")
        )
    }

    @ExceptionHandler(Exception::class)
    open fun handleException(ex: Exception) : ResponseEntity<ResponseDto<Map<String, String>?>> {
        ex.printStackTrace()

        return ResponseEntity.internalServerError().body(
            ResponseDto(code = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                data = null,
                message = "Server Error: ${ex.message ?: "Generic Error"}"))
    }
}