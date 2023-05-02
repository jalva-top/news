package com.example.news.api.advice

import com.example.news.api.response.ErrorResponse
import com.example.news.exception.AlreadyExistException
import com.example.news.exception.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionsApi {
    @ExceptionHandler(NotFoundException::class)
    fun entityNotFoundException(ex: NotFoundException): ResponseEntity<ErrorResponse> =
        ex.toErrorResponse(HttpStatus.NOT_FOUND)

    @ExceptionHandler(AlreadyExistException::class)
    fun alreadyExistException(ex: AlreadyExistException): ResponseEntity<ErrorResponse> =
        ex.toErrorResponse(HttpStatus.CONFLICT)

    @ExceptionHandler(IllegalArgumentException::class)
    fun illegalArgumentException(ex: IllegalArgumentException): ResponseEntity<ErrorResponse> =
        ex.toErrorResponse(HttpStatus.BAD_REQUEST)

    @ExceptionHandler(BindException::class)
    fun bindException(ex: BindException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse(ex.allErrors.mapNotNull { it.defaultMessage }))

    private fun Exception.toErrorResponse(status: HttpStatus): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(status).body(ErrorResponse(listOf(message ?: "${javaClass.simpleName} happened")))
}
